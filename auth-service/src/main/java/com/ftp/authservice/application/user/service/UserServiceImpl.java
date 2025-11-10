package com.ftp.authservice.application.user.service;

import com.ftp.authservice.application.user.command.CreateUserCommand;
import com.ftp.authservice.application.user.command.UpdateUserCommand;
import com.ftp.authservice.application.user.mapper.UserAppMapper;
import com.ftp.authservice.application.user.validation.CreateValidator;
import com.ftp.authservice.application.user.validation.UpdateValidator;
import com.ftp.authservice.domain.model.User;
import com.ftp.authservice.domain.ports.in.user.*;
import com.ftp.authservice.domain.ports.out.user.UserCrudPort;
import com.ftp.authservice.domain.ports.out.user.UserSearchPort;
import com.sharedlib.core.application.service.CrudApplicationService;
import com.sharedlib.core.context.CurrentUserContext;
import com.sharedlib.core.exception.NotFoundException;
import com.sharedlib.core.filter.FilterRequest;
import com.sharedlib.core.i18n.MessageResolver;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl
        extends CrudApplicationService<UUID, User, CreateUserCommand, UpdateUserCommand, User, FilterRequest>
        implements SaveUseCase, UpdateUseCase, LoadUseCase, DeleteUseCase, LoadAllUseCase {

    private final CreateValidator createUserValidator;
    private final UpdateValidator updateUserValidator;
    private final MessageResolver messageResolver;
    private final PasswordEncoder passwordEncoder;
    private final UserAppMapper mapper;
    private final UserCrudPort crudPort;

    public UserServiceImpl(UserCrudPort crudPort,
                           UserSearchPort searchPort,
                           UserAppMapper mapper,
                           CreateValidator createUserValidator,
                           UpdateValidator updateUserValidator,
                           MessageResolver messageResolver,
                           PasswordEncoder passwordEncoder) {
        super(crudPort, searchPort, mapper);
        this.crudPort = crudPort;
        this.mapper = mapper;
        this.createUserValidator = createUserValidator;
        this.updateUserValidator = updateUserValidator;
        this.messageResolver = messageResolver;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected User beforeCreate(User user) {
        if (user.getCreatedById() == null && CurrentUserContext.get() != null) {
            user.setCreatedById(CurrentUserContext.get().userId());
        }
        return user;
    }

    @Override
    protected User beforeUpdate(User current, UpdateUserCommand cmd) {
        if (CurrentUserContext.get() != null) {
            current.setUpdatedById(CurrentUserContext.get().userId());
        }
        // Hash password if present in command
        if (cmd.getPassword() != null && !cmd.getPassword().isBlank()) {
            current.setPasswordHash(passwordEncoder.encode(cmd.getPassword()));
        }
        updateUserValidator.validate(current);
        return current;
    }

    @Override
    protected void afterSave(User saved) {
        // no-op
    }

    @Override
    protected NotFoundException notFound(UUID id) {
        return new NotFoundException(
                messageResolver.getMessage("error.user.not-found", new Object[]{id.toString()})
        );
    }

    @Override
    @Transactional
    public User saveUser(CreateUserCommand command) {
        // 1) validate raw input (command carries raw password)
        createUserValidator.validate(command);

        // 2) hash
        String hashed = passwordEncoder.encode(command.getPassword());

        // 3) map to domain (domain must NEVER carry raw password)
        User toCreate = mapper.fromCreate(command);
        toCreate.setPasswordHash(hashed);

        // 4) audit
        if (toCreate.getCreatedById() == null && CurrentUserContext.get() != null) {
            toCreate.setCreatedById(CurrentUserContext.get().userId());
        }

        // 5) persist
        return crudPort.save(toCreate);
    }

    @Override
    @Transactional
    public User updateUser(UpdateUserCommand command) {
        return update(command.getUserId(), command);
    }

    @Override
    public Optional<User> getUserById(UUID userId) {
        return Optional.ofNullable(getById(userId));
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        if (crudPort.load(userId).isEmpty()) {
            throw notFound(userId);
        }
        delete(userId);
    }

    @Override
    public Page<User> loadAllUsers(FilterRequest filterRequest, Pageable pageable) {
        return search(filterRequest, pageable);
    }
}

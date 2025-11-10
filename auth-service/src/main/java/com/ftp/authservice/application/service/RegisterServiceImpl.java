package com.ftp.authservice.application.service;

import com.ftp.authservice.application.command.RegisterUserCommand;
import com.ftp.authservice.domain.model.User;
import com.ftp.authservice.domain.model.User.AuthMethod;
import com.ftp.authservice.domain.model.UserType;
import com.ftp.authservice.domain.ports.in.RegisterUserUseCase;
import com.ftp.authservice.domain.ports.out.LoadUserPort;
import com.ftp.authservice.domain.ports.out.SaveUserPort;
import com.ftp.authservice.exception.UserAlreadyExistsException;
import com.ftp.authservice.infrastructure.db.entities.PasswordHistoryEntity;
import com.ftp.authservice.infrastructure.db.repositories.PasswordHistoryJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class RegisterServiceImpl implements RegisterUserUseCase {

    private final SaveUserPort saveUserPort;
    private final LoadUserPort loadUserPort;
    private final PasswordEncoder passwordEncoder;
    private final PasswordHistoryJpaRepository passwordHistoryRepository;
    
    private static final int PASSWORD_EXPIRY_DAYS = 60;

    public RegisterServiceImpl(SaveUserPort saveUserPort,
                               LoadUserPort loadUserPort,
                               PasswordEncoder passwordEncoder,
                               PasswordHistoryJpaRepository passwordHistoryRepository) {
        this.saveUserPort = saveUserPort;
        this.loadUserPort = loadUserPort;
        this.passwordEncoder = passwordEncoder;
        this.passwordHistoryRepository = passwordHistoryRepository;
    }

    @Override
    @Transactional
    @SuppressWarnings("deprecation")
    public User register(RegisterUserCommand command) {
        log.info("Registering new user with email: {}", command.getEmail());
        
        if (loadUserPort.loadUserByEmail(command.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(command.getPassword());
        
        // Calculate password expiry (60 days from now)
        Instant now = Instant.now();
        Instant passwordExpiresAt = now.plus(PASSWORD_EXPIRY_DAYS, ChronoUnit.DAYS);

        // Build user with LOCAL auth method
        User newUser = User.builder()
                .firstName(command.getFirstName())
                .fatherName(command.getFatherName())
                .surName(command.getSurname())
                .fullName(command.getFullName())
                .email(command.getEmail().toLowerCase().trim())
                .passwordHash(hashedPassword)
                .authMethod(AuthMethod.LOCAL.name()) // Set auth method to LOCAL
                .passwordChangedAt(now) // Track when password was set
                .passwordExpiresAt(passwordExpiresAt) // Set expiry date
                .mustChangePassword(false) // New users don't need immediate change
                .type(UserType.valueOf(command.getType()))
                .enabled(true)
                .deleted(false)
                .language(command.getLanguage())
                .createdAt(now)
                .lastLogin(now)
                .profileImageUrl(null)
                .createdById(null)
                .updatedById(null)
                .rowVersion(null)
                .isActive(true)
                .isDeleted(false)
                .build();

        // Save user
        User savedUser = saveUserPort.saveUser(newUser);
        
        // Create password history record
        try {
            PasswordHistoryEntity passwordHistory = PasswordHistoryEntity.builder()
                    .userId(savedUser.getId())
                    .passwordHash(hashedPassword)
                    .changedAt(now)
                    .build();
            passwordHistoryRepository.save(passwordHistory);
            log.info("Created password history record for user: {}", savedUser.getEmail());
        } catch (Exception e) {
            log.warn("Failed to create password history record: {}", e.getMessage());
            // Continue even if password history fails
        }
        
        log.info("Successfully registered user: {} with ID: {}", savedUser.getEmail(), savedUser.getId());
        return savedUser;
    }
}

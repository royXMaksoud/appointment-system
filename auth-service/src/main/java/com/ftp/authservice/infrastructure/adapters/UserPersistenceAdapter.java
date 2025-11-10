package com.ftp.authservice.infrastructure.adapters;

import com.ftp.authservice.domain.model.User;
import com.ftp.authservice.domain.ports.out.LoadUserPort;
import com.ftp.authservice.domain.ports.out.SaveUserPort;
import com.ftp.authservice.infrastructure.db.entities.UserJpaEntity;
import com.ftp.authservice.infrastructure.db.mappers.UserJpaMapper;
import com.ftp.authservice.infrastructure.db.repositories.UserRepository;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserPersistenceAdapter implements SaveUserPort , LoadUserPort {

    private final UserRepository userRepository;


    public UserPersistenceAdapter(UserRepository userRepository, EntityManagerFactoryInfo entityManagerFactoryInfo) {
        this.userRepository = userRepository;

    }
    @Override
    public User saveUser(User user){
        UserJpaEntity entity= UserJpaMapper.toJpaEntity(user);
        UserJpaEntity savedUserEntity =userRepository.save(entity);
        return UserJpaMapper.toDomainEntity(savedUserEntity );
    }
    @Override
    public  Optional<User> loadUserByEmail(String email){
        return userRepository.findByEmailIgnoreCaseAndEnabledTrueAndDeletedFalse(email)
                .map(UserJpaMapper::toDomainEntity);
    }


    @Override
    public Optional<User> loadUserById(UUID id) {
        return userRepository.findById(id)
                .map(UserJpaMapper::toDomainEntity);
    }



}

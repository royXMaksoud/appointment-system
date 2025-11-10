package com.ftp.authservice.infrastructure.db.repositories;

import com.ftp.authservice.infrastructure.db.entities.RefreshTokenJpaEntity;
import com.ftp.authservice.infrastructure.db.entities.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {

    // Find refresh token by token string
    Optional<RefreshTokenJpaEntity> findByToken(String token);

    // Delete all refresh tokens for a given user
    void deleteAllByUser(UserJpaEntity user);

    // Optionally, find all tokens for a user (for security/logging)
    List<RefreshTokenJpaEntity> findAllByUser(UserJpaEntity user);

    void deleteByToken(String token);

}

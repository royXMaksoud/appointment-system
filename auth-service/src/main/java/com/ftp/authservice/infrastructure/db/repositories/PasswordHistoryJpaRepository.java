package com.ftp.authservice.infrastructure.db.repositories;

import com.ftp.authservice.infrastructure.db.entities.PasswordHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PasswordHistoryJpaRepository extends JpaRepository<PasswordHistoryEntity, UUID> {
    List<PasswordHistoryEntity> findTop5ByUserIdOrderByChangedAtDesc(UUID userId);
}


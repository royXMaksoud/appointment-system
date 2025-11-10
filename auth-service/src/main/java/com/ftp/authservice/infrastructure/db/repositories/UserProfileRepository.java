package com.ftp.authservice.infrastructure.db.repositories;

import com.ftp.authservice.infrastructure.db.entities.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, UUID> {
    Optional<UserProfileEntity> findByUserId(UUID userId);
    List<UserProfileEntity> findAllByUserIdIn(Collection<UUID> userIds);
}

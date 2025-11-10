package com.ftp.authservice.infrastructure.db.repositories;

import com.ftp.authservice.infrastructure.db.entities.IdentityProviderAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing Identity Provider Account entities (OAuth/IDP linking)
 */
@Repository
public interface IdentityProviderAccountRepository extends JpaRepository<IdentityProviderAccountEntity, UUID> {
    
    /**
     * Find IDP account by provider and provider user ID
     * @param provider Provider name (GOOGLE, MICROSOFT, etc.)
     * @param providerUserId Unique user ID from provider
     * @return Optional IDP account entity
     */
    Optional<IdentityProviderAccountEntity> findByProviderAndProviderUserId(String provider, String providerUserId);
    
    /**
     * Find IDP account by provider and email
     * @param provider Provider name
     * @param providerEmail Email from provider
     * @return Optional IDP account entity
     */
    Optional<IdentityProviderAccountEntity> findByProviderAndProviderEmail(String provider, String providerEmail);
    
    /**
     * Find all IDP accounts for a user
     * @param userId User ID
     * @return List of IDP account entities
     */
    java.util.List<IdentityProviderAccountEntity> findByUserId(UUID userId);
    
    /**
     * Check if IDP account exists for provider and user ID
     * @param provider Provider name
     * @param providerUserId Provider user ID
     * @return True if exists
     */
    boolean existsByProviderAndProviderUserId(String provider, String providerUserId);
}


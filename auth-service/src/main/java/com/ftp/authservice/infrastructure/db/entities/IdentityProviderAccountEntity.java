package com.ftp.authservice.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "identity_provider_accounts",
    uniqueConstraints = {
        @UniqueConstraint(name = "ux_idp_provider_user", columnNames = {"provider", "provider_user_id"})
    },
    indexes = {
        @Index(name = "ix_idp_user", columnList = "user_id"),
        @Index(name = "ix_idp_provider", columnList = "provider")
    }
)
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class IdentityProviderAccountEntity {

    @Id
    @Column(name = "identity_provider_account_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID identityProviderAccountId;

    /** FK -> users.id */
    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    /** e.g., GOOGLE, MICROSOFT, AZURE_AD, GITHUB */
    @Column(name = "provider", nullable = false, length = 50)
    private String provider;

    /** Unique subject/oid/uid from the provider */
    @Column(name = "provider_user_id", nullable = false, length = 200)
    private String providerUserId;

    @Column(name = "provider_email", length = 200)
    private String providerEmail;

    @Column(name = "is_email_verified")
    private Boolean isEmailVerified;

    @CreationTimestamp
    @Column(name = "linked_at", updatable = false, nullable = false)
    private Instant linkedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    void pp() {
        if (identityProviderAccountId == null) identityProviderAccountId = UUID.randomUUID();
        if (isEmailVerified == null) isEmailVerified = Boolean.FALSE;
    }
}

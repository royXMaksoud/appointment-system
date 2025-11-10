package com.ftp.authservice.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "user_profiles",
        schema = "public",
        indexes = {
                @Index(name = "ix_user_profiles_user_id", columnList = "user_id", unique = true)
        }
)
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserProfileEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "user_profile_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID userProfileId;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "tenant_id", columnDefinition = "uuid")
    private UUID tenantId;

    @Column(name = "company_id", columnDefinition = "uuid")
    private UUID companyId;

    @Column(name = "country_id", columnDefinition = "uuid")
    private UUID countryId;

    @Column(name = "city_id", columnDefinition = "uuid")
    private UUID cityId;

    @Column(name = "preferred_language", length = 10)
    private String preferredLanguage;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    /** Optimistic locking (BIGINT) */
    @Version
    @Column(name = "row_version")
    private Long rowVersion;

    @PrePersist
    void onCreate() {
        if (userProfileId == null) userProfileId = UUID.randomUUID();
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}



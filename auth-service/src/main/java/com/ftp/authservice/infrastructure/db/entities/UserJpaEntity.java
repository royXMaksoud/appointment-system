package com.ftp.authservice.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email", columnNames = "email")
    },
    indexes = {
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_account_kind", columnList = "account_kind"),
        @Index(name = "idx_users_enabled_deleted", columnList = "enabled,deleted"),
        @Index(name = "idx_users_validity", columnList = "valid_from,valid_to"),
        @Index(name = "idx_users_pwd_exp", columnList = "password_expires_at"),
        @Index(name = "idx_users_tenant", columnList = "tenant_id"),
        @Index(name = "idx_users_org", columnList = "organization_id"),
        @Index(name = "idx_users_org_branch", columnList = "organization_branch_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJpaEntity {

    public enum AccountKind {
        GENERAL,   // consumer/basic user
        OPERATOR,  // staff/operator of business modules
        ADMIN      // platform/tenant admin
    }
    public enum AuthMethod { LOCAL, OAUTH }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_method", nullable = false, length = 20)
    private AuthMethod authMethod;

    @Column(name = "last_auth_provider", length = 50)
    private String lastAuthProvider;

    // --- Current affiliation (lightweight, optional) ---
    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(name = "organization_branch_id")
    private UUID organizationBranchId;

    // --- Name parts ---
    @Column(length = 100)
    private String firstName;

    @Column(length = 100)
    private String fatherName;

    @Column(length = 100)
    private String surName;

    // Denormalized (rebuilt on persist/update)
    @Column(length = 300)
    private String fullName;

    // --- Contact ---
    @Column(nullable = false, length = 200)
    private String email;

    @Column(name = "is_email_verified")
    private Boolean isEmailVerified;

    // --- Auth ---
    // Nullable to support OAuth/AD users (no local password)
    @Column(name = "password_hash", length = 100)
    private String password;

    // Password lifecycle
    @Column(name = "password_changed_at")
    private Instant passwordChangedAt;

    @Column(name = "password_expires_at")
    private Instant passwordExpiresAt;

    @Column(name = "must_change_password")
    private Boolean mustChangePassword;

    // --- Preferences ---
    @Column(name = "language", length = 10)
    private String language;

    // High-level account class
    @Enumerated(EnumType.STRING)
    @Column(name = "account_kind", nullable = false, length = 20)
    private AccountKind accountKind;

    @Deprecated
    @Column(name = "type", length = 50)
    private String type;

    // --- Employment / Account validity ---
    @Column(name = "employment_start_date")
    private LocalDate employmentStartDate;

    @Column(name = "employment_end_date")
    private LocalDate employmentEndDate;

    @Column(name = "valid_from")
    private Instant validFrom;

    @Column(name = "valid_to")
    private Instant validTo;

    @Column(name = "must_renew_at")
    private Instant mustRenewAt;

    // --- Status ---
    @Column(nullable = false)
    private boolean enabled;

    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;

    @Column(name = "last_login")
    private Instant lastLogin;

    // Optional presentation
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;
    

    // --- Audit ---
    @Column(name = "created_by_user_id")
    private UUID createdById;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "updated_by_user_id")
    private UUID updatedById;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(name = "row_version")
    private Long rowVersion;

    // --- Hooks ---
    @PrePersist
    public void prePersist() {
        if (language == null) language = "en";
        if (accountKind == null) accountKind = AccountKind.GENERAL;
        if (authMethod == null) authMethod = AuthMethod.LOCAL;
        if (isEmailVerified == null) isEmailVerified = Boolean.FALSE;
        if (mustChangePassword == null) mustChangePassword = Boolean.FALSE;

        if (email != null) email = email.trim().toLowerCase();
        this.fullName = buildFullName();

        Instant now = Instant.now();
        if (validFrom == null) validFrom = now;
        if (lastLogin == null) lastLogin = now; // keep your original behavior
    }

    @PreUpdate
    public void preUpdate() {
        if (email != null) email = email.trim().toLowerCase();
        this.fullName = buildFullName();
    }

    private String buildFullName() {
        StringBuilder sb = new StringBuilder();
        if (firstName != null && !firstName.isBlank()) sb.append(firstName.trim());
        if (fatherName != null && !fatherName.isBlank()) sb.append(' ').append(fatherName.trim());
        if (surName != null && !surName.isBlank()) sb.append(' ').append(surName.trim());
        return sb.toString().trim();
    }

    // --- Convenience ---
    @Transient
    public boolean isExpired() {
        return validTo != null && Instant.now().isAfter(validTo);
    }

    @Transient
    public boolean needsRenewal() {
        return mustRenewAt != null && Instant.now().isAfter(mustRenewAt);
    }

    @Transient
    public boolean isPasswordExpired() {
        return passwordExpiresAt != null && Instant.now().isAfter(passwordExpiresAt);
    }
}

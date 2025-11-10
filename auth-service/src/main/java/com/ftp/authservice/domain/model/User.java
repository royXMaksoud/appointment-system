package com.ftp.authservice.domain.model;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    // Enums expected by controllers (kept in domain model for API typing)
    public enum AuthMethod { LOCAL, OAUTH }
    public enum AccountKind { GENERAL, OPERATOR, ADMIN }

    private UUID id;

    // --- Basic Info ---
    private String firstName;
    private String fatherName;
    private String surName;
    private String fullName;

    // --- Contact ---
    private String email;
    private Boolean isEmailVerified;

    // --- Authentication ---
    private String passwordHash;
    private String authMethod; // LOCAL, OAUTH, FEDERATED_AD
    private String lastAuthProvider; // google, microsoft, etc.

    // --- Password Lifecycle ---
    private Instant passwordChangedAt;
    private Instant passwordExpiresAt;
    private Boolean mustChangePassword;

    // --- Organization & Tenant ---
    private UUID tenantId;
    private UUID organizationId;
    private UUID organizationBranchId;

    // --- Account Classification ---
    private String accountKind; // GENERAL, OPERATOR, ADMIN
    
    @Deprecated
    private UserType type; // backward compatibility

    // --- Status ---
    private boolean enabled;
    private boolean deleted;

    // --- Account Validity ---
    private Instant validFrom;
    private Instant validTo;
    private Instant mustRenewAt;

    // --- Employment ---
    private LocalDate employmentStartDate;
    private LocalDate employmentEndDate;

    // --- Preferences ---
    private String language;
    private String profileImageUrl;

    // --- Timestamps ---
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLogin;

    // --- Audit ---
    private UUID createdById;
    private UUID updatedById;

    // --- Optimistic locking ---
    private Long rowVersion;

    // --- Optional flags for backward compatibility ---
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;

    @Builder.Default
    private Boolean isDeleted = Boolean.FALSE;

public boolean isPasswordExpired() {
    return passwordExpiresAt != null && java.time.Instant.now().isAfter(passwordExpiresAt);
}

public boolean needsRenewal() {
    return mustRenewAt != null && java.time.Instant.now().isAfter(mustRenewAt);
}
}

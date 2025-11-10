package com.ftp.authservice.web.dto.user;

import com.ftp.authservice.domain.model.UserType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * API response model representing a user.
 * Used in GET, CREATE, UPDATE, and SEARCH endpoints.
 */
@Getter
@Setter
@Builder
public class UserResponse {

    // --- Identifiers ---
    private UUID id;

    // --- Basic Info ---
    private String firstName;
    private String fatherName;
    private String surName;
    private String fullName;

    // --- Contact ---
    private String emailAddress;
    private Boolean isEmailVerified;

    // --- Authentication ---
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
    private Boolean enabled;
    private Boolean deleted;

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

    // --- Metadata ---
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLogin;

    private UUID createdById;
    private UUID updatedById;

    private Long rowVersion;
}

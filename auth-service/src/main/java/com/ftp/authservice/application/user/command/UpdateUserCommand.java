package com.ftp.authservice.application.user.command;

import com.ftp.authservice.domain.model.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Command to update an existing User.
 * Only non-null fields will be applied during the update.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserCommand {

    /** Required target user ID. */
    private UUID userId;

    // --- Personal Info ---
    private String firstName;
    private String fatherName;
    private String surName;
    private String fullName;

    // --- Contact Info ---
    private String email;
    private Boolean isEmailVerified;

    // --- Authentication ---
    private String password; // raw new password (to be re-encoded)
    private String authMethod;
    private String lastAuthProvider;

    // --- Password Lifecycle ---
    private Instant passwordExpiresAt;
    private Boolean mustChangePassword;

    // --- Organization & Tenant ---
    private UUID tenantId;
    private UUID organizationId;
    private UUID organizationBranchId;

    // --- Account Classification ---
    private String accountKind;
    
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

    // --- Audit ---
    private UUID updatedById;
    
    // --- Optimistic locking ---
    private Long rowVersion;
}

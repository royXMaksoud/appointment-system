package com.ftp.authservice.application.user.command;

import com.ftp.authservice.domain.model.UserType;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Command used to create a new User.
 * Only validated data from the Web layer reaches this level.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserCommand {

    /** Optional: generated automatically if null. */
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
    /** Raw password (to be encoded by the service). Optional for OAuth users. */
    private String password;
    private String authMethod; // LOCAL, OAUTH, FEDERATED_AD
    private String lastAuthProvider;

    // --- Password Lifecycle ---
    private Instant passwordExpiresAt;
    private Boolean mustChangePassword;

    // --- Organization & Tenant ---
    private UUID tenantId;
    private UUID organizationId;
    private UUID organizationBranchId;

    // --- Account Classification ---
    private String accountKind; // GENERAL, OPERATOR, ADMIN
    
    @Deprecated
    @Builder.Default
    private UserType type = UserType.USER; // backward compatibility

    // --- Status ---
    @Builder.Default
    private Boolean enabled = true;

    @Builder.Default
    private Boolean deleted = false;

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
    private UUID createdById;
}

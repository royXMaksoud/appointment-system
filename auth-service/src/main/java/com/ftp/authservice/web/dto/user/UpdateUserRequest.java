package com.ftp.authservice.web.dto.user;

import com.ftp.authservice.domain.model.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for updating an existing user.
 * Only non-null fields will be applied during update.
 */
@Getter
@Setter
public class UpdateUserRequest {

    // --- Name fields ---
    @Size(max = 100, message = "{user.firstName.max}")
    private String firstName;

    @Size(max = 100, message = "{user.fatherName.max}")
    private String fatherName;

    @Size(max = 100, message = "{user.surName.max}")
    private String surName;

    // --- Contact ---
    @Email(message = "{user.email.invalid}")
    @Size(max = 200, message = "{user.email.max}")
    private String emailAddress;

    private Boolean isEmailVerified;

    // --- Auth ---
    @Size(min = 3, max = 128, message = "{user.password.size}")
    private String password;

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
    @Size(max = 10, message = "{user.language.max}")
    private String language;

    // --- Optional presentation ---
    @Size(max = 500, message = "{user.profileImageUrl.max}")
    private String profileImageUrl;

    // --- Optimistic locking ---
    private Long rowVersion;
}

package com.ftp.authservice.web.dto.user;

import com.ftp.authservice.domain.model.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateUserRequest {

    // --- Basic Info ---
    @NotBlank(message = "{user.firstName.required}")
    @Size(max = 100, message = "{user.firstName.max}")
    private String firstName;

    @Size(max = 100, message = "{user.fatherName.max}")
    private String fatherName;

    @Size(max = 100, message = "{user.surName.max}")
    private String surName;

    // --- Contact Info ---
    @NotBlank(message = "{user.email.required}")
    @Email(message = "{user.email.invalid}")
    @Size(max = 200, message = "{user.email.max}")
    private String emailAddress;

    private Boolean isEmailVerified;

    // --- Authentication ---
    // Password is now optional (for OAuth users)
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
    @NotNull(message = "{user.accountKind.required}")
    private String accountKind; // GENERAL, OPERATOR, ADMIN

    @Deprecated
    private UserType type; // backward compatibility

    // --- Account Status ---
    @NotNull(message = "{user.enabled.required}")
    private Boolean enabled;

    private Boolean deleted = false;

    // --- Account Validity ---
    private Instant validFrom;
    private Instant validTo;
    private Instant mustRenewAt;

    // --- Employment ---
    private LocalDate employmentStartDate;
    private LocalDate employmentEndDate;

    // --- Localization / Preferences ---
    @Size(max = 10, message = "{user.language.max}")
    private String language = "en";

    // --- Optional Presentation ---
    private String profileImageUrl;
}
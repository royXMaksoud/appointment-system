package com.ftp.authservice.application.user.mapper;

import com.ftp.authservice.application.user.command.CreateUserCommand;
import com.ftp.authservice.application.user.command.UpdateUserCommand;
import com.ftp.authservice.domain.model.User;
import com.sharedlib.core.application.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.Instant;

/**
 * Mapper for converting Create/Update commands into User domain model.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserAppMapper extends BaseMapper<User, CreateUserCommand, UpdateUserCommand, User> {

    /**
     * Convert CreateUserCommand -> User domain.
     */
    @Override
    default User fromCreate(CreateUserCommand cmd) {
        // Determine auth method: if password is provided, it's LOCAL, otherwise OAUTH
        String authMethod = (cmd.getPassword() != null && !cmd.getPassword().isBlank()) 
            ? "LOCAL" 
            : "OAUTH";
            
        return User.builder()
                .id(cmd.getUserId())
                // Basic Info
                .firstName(cmd.getFirstName())
                .fatherName(cmd.getFatherName())
                .surName(cmd.getSurName())
                .fullName(cmd.getFullName())
                // Contact
                .email(cmd.getEmail())
                .isEmailVerified(cmd.getIsEmailVerified())
                // Authentication
                .passwordHash(null) // hash will be set later in the service
                .authMethod(cmd.getAuthMethod() != null ? cmd.getAuthMethod() : authMethod)
                .lastAuthProvider(cmd.getLastAuthProvider())
                // Password Lifecycle
                .passwordExpiresAt(cmd.getPasswordExpiresAt())
                .mustChangePassword(cmd.getMustChangePassword())
                // Organization & Tenant
                .tenantId(cmd.getTenantId())
                .organizationId(cmd.getOrganizationId())
                .organizationBranchId(cmd.getOrganizationBranchId())
                // Account Classification
                .accountKind(cmd.getAccountKind())
                .type(cmd.getType())
                // Status
                .enabled(cmd.getEnabled() != null ? cmd.getEnabled() : Boolean.TRUE)
                .deleted(cmd.getDeleted() != null ? cmd.getDeleted() : Boolean.FALSE)
                // Account Validity
                .validFrom(cmd.getValidFrom() != null ? cmd.getValidFrom() : Instant.now())
                .validTo(cmd.getValidTo())
                .mustRenewAt(cmd.getMustRenewAt())
                // Employment
                .employmentStartDate(cmd.getEmploymentStartDate())
                .employmentEndDate(cmd.getEmploymentEndDate())
                // Preferences
                .language(cmd.getLanguage() != null ? cmd.getLanguage() : "en")
                .profileImageUrl(cmd.getProfileImageUrl())
                // Audit
                .createdById(cmd.getCreatedById())
                .createdAt(Instant.now())
                .lastLogin(Instant.now())
                // Flags
                .isActive(Boolean.TRUE)
                .isDeleted(Boolean.FALSE)
                .rowVersion(0L)
                .build();
    }

    /**
     * Update existing User domain from UpdateUserCommand (ignore nulls).
     */
    @Override
    default void updateDomain(@MappingTarget User target, UpdateUserCommand cmd) {
        // Basic Info
        if (cmd.getFirstName() != null) target.setFirstName(cmd.getFirstName());
        if (cmd.getFatherName() != null) target.setFatherName(cmd.getFatherName());
        if (cmd.getSurName() != null) target.setSurName(cmd.getSurName());
        if (cmd.getFullName() != null) target.setFullName(cmd.getFullName());
        // Contact
        if (cmd.getEmail() != null) target.setEmail(cmd.getEmail());
        if (cmd.getIsEmailVerified() != null) target.setIsEmailVerified(cmd.getIsEmailVerified());
        // Authentication
        // DO NOT set raw password here. Hashing handled in service (beforeUpdate).
        if (cmd.getAuthMethod() != null) target.setAuthMethod(cmd.getAuthMethod());
        if (cmd.getLastAuthProvider() != null) target.setLastAuthProvider(cmd.getLastAuthProvider());
        // Password Lifecycle
        if (cmd.getPasswordExpiresAt() != null) target.setPasswordExpiresAt(cmd.getPasswordExpiresAt());
        if (cmd.getMustChangePassword() != null) target.setMustChangePassword(cmd.getMustChangePassword());
        // Organization & Tenant
        if (cmd.getTenantId() != null) target.setTenantId(cmd.getTenantId());
        if (cmd.getOrganizationId() != null) target.setOrganizationId(cmd.getOrganizationId());
        if (cmd.getOrganizationBranchId() != null) target.setOrganizationBranchId(cmd.getOrganizationBranchId());
        // Account Classification
        if (cmd.getAccountKind() != null) target.setAccountKind(cmd.getAccountKind());
        if (cmd.getType() != null) target.setType(cmd.getType());
        // Status
        if (cmd.getEnabled() != null) target.setEnabled(cmd.getEnabled());
        if (cmd.getDeleted() != null) target.setDeleted(cmd.getDeleted());
        // Account Validity
        if (cmd.getValidFrom() != null) target.setValidFrom(cmd.getValidFrom());
        if (cmd.getValidTo() != null) target.setValidTo(cmd.getValidTo());
        if (cmd.getMustRenewAt() != null) target.setMustRenewAt(cmd.getMustRenewAt());
        // Employment
        if (cmd.getEmploymentStartDate() != null) target.setEmploymentStartDate(cmd.getEmploymentStartDate());
        if (cmd.getEmploymentEndDate() != null) target.setEmploymentEndDate(cmd.getEmploymentEndDate());
        // Preferences
        if (cmd.getLanguage() != null) target.setLanguage(cmd.getLanguage());
        if (cmd.getProfileImageUrl() != null) target.setProfileImageUrl(cmd.getProfileImageUrl());
        // Audit
        if (cmd.getUpdatedById() != null) target.setUpdatedById(cmd.getUpdatedById());
        target.setUpdatedAt(Instant.now());
    }
    /**
     * Convert User domain -> response (identity mapping).
     */
    @Override
    default User toResponse(User domain) {
        return domain; // direct pass-through
    }
}

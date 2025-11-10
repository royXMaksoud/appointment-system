package com.ftp.authservice.infrastructure.db.mappers;

import com.ftp.authservice.domain.model.User;
import com.ftp.authservice.domain.model.UserType;
import com.ftp.authservice.infrastructure.db.entities.UserJpaEntity;

public class UserJpaMapper {

    public static UserJpaEntity toJpaEntity(User user) {
        if (user == null) return null;

        return UserJpaEntity.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .fatherName(user.getFatherName())
                .surName(user.getSurName())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .isEmailVerified(user.getIsEmailVerified())
                .password(user.getPasswordHash())
                .authMethod(user.getAuthMethod() != null ? 
                    UserJpaEntity.AuthMethod.valueOf(user.getAuthMethod()) : UserJpaEntity.AuthMethod.LOCAL)
                .lastAuthProvider(user.getLastAuthProvider())
                .tenantId(user.getTenantId())
                .organizationId(user.getOrganizationId())
                .organizationBranchId(user.getOrganizationBranchId())
                .accountKind(user.getAccountKind() != null ? 
                    UserJpaEntity.AccountKind.valueOf(user.getAccountKind()) : UserJpaEntity.AccountKind.GENERAL)
                .passwordChangedAt(user.getPasswordChangedAt())
                .passwordExpiresAt(user.getPasswordExpiresAt())
                .mustChangePassword(user.getMustChangePassword())
                .validFrom(user.getValidFrom())
                .validTo(user.getValidTo())
                .mustRenewAt(user.getMustRenewAt())
                .employmentStartDate(user.getEmploymentStartDate())
                .employmentEndDate(user.getEmploymentEndDate())
                .language(user.getLanguage())
                .type(user.getType() != null ? user.getType().name() : null)
                .enabled(user.isEnabled())
                .deleted(user.isDeleted())
                .lastLogin(user.getLastLogin())
                .profileImageUrl(user.getProfileImageUrl())
                .createdById(user.getCreatedById())
                .createdAt(user.getCreatedAt())
                .updatedById(user.getUpdatedById())
                .updatedAt(user.getUpdatedAt())
                .rowVersion(user.getRowVersion())
                .build();
    }

    public static User toDomainEntity(UserJpaEntity entity) {
        if (entity == null) return null;

        return User.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .fatherName(entity.getFatherName())
                .surName(entity.getSurName())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .isEmailVerified(entity.getIsEmailVerified())
                .passwordHash(entity.getPassword())
                .authMethod(entity.getAuthMethod() != null ? entity.getAuthMethod().name() : "LOCAL")
                .lastAuthProvider(entity.getLastAuthProvider())
                .tenantId(entity.getTenantId())
                .organizationId(entity.getOrganizationId())
                .organizationBranchId(entity.getOrganizationBranchId())
                .accountKind(entity.getAccountKind() != null ? entity.getAccountKind().name() : "GENERAL")
                .passwordChangedAt(entity.getPasswordChangedAt())
                .passwordExpiresAt(entity.getPasswordExpiresAt())
                .mustChangePassword(entity.getMustChangePassword())
                .validFrom(entity.getValidFrom())
                .validTo(entity.getValidTo())
                .mustRenewAt(entity.getMustRenewAt())
                .employmentStartDate(entity.getEmploymentStartDate())
                .employmentEndDate(entity.getEmploymentEndDate())
                .language(entity.getLanguage())
                .type(entity.getType() != null ? UserType.valueOf(entity.getType()) : null)
                .enabled(entity.isEnabled())
                .deleted(entity.isDeleted())
                .lastLogin(entity.getLastLogin())
                .profileImageUrl(entity.getProfileImageUrl())
                .createdById(entity.getCreatedById())
                .createdAt(entity.getCreatedAt())
                .updatedById(entity.getUpdatedById())
                .updatedAt(entity.getUpdatedAt())
                .rowVersion(entity.getRowVersion())
                .isActive(Boolean.TRUE)   // maintain consistency
                .isDeleted(Boolean.FALSE) // maintain consistency
                .build();
    }
}

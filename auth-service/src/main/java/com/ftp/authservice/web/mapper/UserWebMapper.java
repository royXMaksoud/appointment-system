package com.ftp.authservice.web.mapper;

import com.ftp.authservice.application.user.command.CreateUserCommand;
import com.ftp.authservice.application.user.command.UpdateUserCommand;
import com.ftp.authservice.domain.model.User;
import com.ftp.authservice.web.dto.user.CreateUserRequest;
import com.ftp.authservice.web.dto.user.UpdateUserRequest;
import com.ftp.authservice.web.dto.user.UserResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserWebMapper {

    public CreateUserCommand toCreateCommand(CreateUserRequest request) {
        return CreateUserCommand.builder()
                .firstName(request.getFirstName())
                .fatherName(request.getFatherName())
                .surName(request.getSurName())
                .email(request.getEmailAddress())
                .isEmailVerified(request.getIsEmailVerified())
                .password(request.getPassword())
                .passwordExpiresAt(request.getPasswordExpiresAt())
                .mustChangePassword(request.getMustChangePassword())
                .tenantId(request.getTenantId())
                .organizationId(request.getOrganizationId())
                .organizationBranchId(request.getOrganizationBranchId())
                .accountKind(request.getAccountKind())
                .type(request.getType())
                .enabled(request.getEnabled())
                .validFrom(request.getValidFrom())
                .validTo(request.getValidTo())
                .mustRenewAt(request.getMustRenewAt())
                .employmentStartDate(request.getEmploymentStartDate())
                .employmentEndDate(request.getEmploymentEndDate())
                .language(request.getLanguage())
                .profileImageUrl(request.getProfileImageUrl())
                .build();
    }

    public UpdateUserCommand toUpdateCommand(UUID userId, UpdateUserRequest request) {
        return UpdateUserCommand.builder()
                .userId(userId)
                .firstName(request.getFirstName())
                .fatherName(request.getFatherName())
                .surName(request.getSurName())
                .email(request.getEmailAddress())
                .isEmailVerified(request.getIsEmailVerified())
                .password(request.getPassword())
                .passwordExpiresAt(request.getPasswordExpiresAt())
                .mustChangePassword(request.getMustChangePassword())
                .tenantId(request.getTenantId())
                .organizationId(request.getOrganizationId())
                .organizationBranchId(request.getOrganizationBranchId())
                .accountKind(request.getAccountKind())
                .type(request.getType())
                .enabled(request.getEnabled())
                .deleted(request.getDeleted())
                .validFrom(request.getValidFrom())
                .validTo(request.getValidTo())
                .mustRenewAt(request.getMustRenewAt())
                .employmentStartDate(request.getEmploymentStartDate())
                .employmentEndDate(request.getEmploymentEndDate())
                .language(request.getLanguage())
                .profileImageUrl(request.getProfileImageUrl())
                .rowVersion(request.getRowVersion())
                .build();
    }

    public UserResponse toResponse(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .fatherName(user.getFatherName())
                .surName(user.getSurName())
                .fullName(user.getFullName())
                .emailAddress(user.getEmail())
                .isEmailVerified(user.getIsEmailVerified())
                .authMethod(user.getAuthMethod())
                .lastAuthProvider(user.getLastAuthProvider())
                .passwordChangedAt(user.getPasswordChangedAt())
                .passwordExpiresAt(user.getPasswordExpiresAt())
                .mustChangePassword(user.getMustChangePassword())
                .tenantId(user.getTenantId())
                .organizationId(user.getOrganizationId())
                .organizationBranchId(user.getOrganizationBranchId())
                .accountKind(user.getAccountKind())
                .type(user.getType())
                .enabled(user.isEnabled())
                .deleted(user.isDeleted())
                .validFrom(user.getValidFrom())
                .validTo(user.getValidTo())
                .mustRenewAt(user.getMustRenewAt())
                .employmentStartDate(user.getEmploymentStartDate())
                .employmentEndDate(user.getEmploymentEndDate())
                .language(user.getLanguage())
                .profileImageUrl(user.getProfileImageUrl())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdById(user.getCreatedById())
                .updatedById(user.getUpdatedById())
                .rowVersion(user.getRowVersion())
                .build();
    }

    public List<UserResponse> toUserResponseList(List<User> users) {
        return users.stream().map(this::toResponse).collect(Collectors.toList());
    }
}

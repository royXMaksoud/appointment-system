package com.ftp.authservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthSuccessResponse {
    private String accessToken;
    private java.util.UUID userId;
    private String email;
    private String fullName;
    private String accountKind;
    private String language;
    private boolean requiresPasswordChange;
    private boolean requiresRenewal;
    private Integer sessionTimeoutMinutes;
    private String tenantLogo;
}

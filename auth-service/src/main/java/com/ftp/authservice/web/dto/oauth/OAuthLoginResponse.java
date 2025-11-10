package com.ftp.authservice.web.dto.oauth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuthLoginResponse {

    private String accessToken;
    private UUID userId;
    private String email;
    private String fullName;

    @JsonProperty("isNewUser")
    private boolean newUser;

    private String provider;
    private String language;
    private String error;
    private String message;
    private Integer sessionTimeoutMinutes;
    private String tenantLogo;
}


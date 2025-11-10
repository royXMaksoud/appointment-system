package com.ftp.authservice.web.dto.oauth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OAuth callback request from frontend
 * Contains authorization code and state from OAuth provider
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuthCallbackRequest {
    
    @NotBlank(message = "Provider is required")
    private String provider; // google, microsoft
    
    @NotBlank(message = "Authorization code is required")
    private String code;
    
    @NotBlank(message = "Redirect URI is required")
    private String redirectUri;
    
    @NotBlank(message = "State is required")
    private String state;
}


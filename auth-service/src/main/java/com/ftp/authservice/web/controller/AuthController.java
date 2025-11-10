package com.ftp.authservice.web.controller;

import com.ftp.authservice.application.command.RegisterUserCommand;
import com.ftp.authservice.application.service.OAuthAccountLinkService;
import com.ftp.authservice.application.service.OAuthService;
import com.ftp.authservice.domain.model.OAuthProvider;
import com.ftp.authservice.domain.model.User;
import com.ftp.authservice.domain.ports.in.LoginUseCase;
import com.ftp.authservice.domain.ports.in.LogoutUseCase;
import com.ftp.authservice.domain.ports.in.RefreshTokenUseCase;
import com.ftp.authservice.domain.ports.in.RegisterUserUseCase;
import com.ftp.authservice.exception.PasswordChangeRequiredException;
import com.ftp.authservice.infrastructure.db.entities.UserJpaEntity;
import com.ftp.authservice.infrastructure.security.JwtTokenProvider;
import com.ftp.authservice.web.dto.AuthSuccessResponse;
import com.ftp.authservice.web.dto.JwtResponseDTO;
import com.ftp.authservice.web.dto.LoginRequestDTO;
import com.ftp.authservice.web.dto.RefreshTokenRequestDTO;
import com.ftp.authservice.web.dto.RegisterRequestDTO;
import com.ftp.authservice.web.dto.oauth.OAuthCallbackRequest;
import com.ftp.authservice.web.dto.oauth.OAuthLoginResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final int DEFAULT_SESSION_TIMEOUT_MINUTES = 30;
    private static final String DEFAULT_ACCESS_SERVICE_BASE_URL = "http://localhost:8082/api/v1";

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final JwtTokenProvider jwtTokenProvider;
    private final LogoutUseCase logoutUseCase;
    private final OAuthService oauthService;
    private final OAuthAccountLinkService oauthAccountLinkService;
    private final RestTemplate restTemplate;

    @Value("${oauth.google.client-id:}")
    private String googleClientId;

    @Value("${oauth.microsoft.client-id:}")
    private String microsoftClientId;

    @Value("${access.service.base-url:http://localhost:6062/api/v1}")
    private String accessServiceBaseUrl;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          LoginUseCase loginUseCase,
                          RefreshTokenUseCase refreshTokenUseCase,
                          JwtTokenProvider jwtTokenProvider,
                          LogoutUseCase logoutUseCase,
                          OAuthService oauthService,
                          OAuthAccountLinkService oauthAccountLinkService,
                          RestTemplateBuilder restTemplateBuilder) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
        this.logoutUseCase = logoutUseCase;
        this.oauthService = oauthService;
        this.oauthAccountLinkService = oauthAccountLinkService;
        this.restTemplate = restTemplateBuilder.build();
    }

    private TenantSettings resolveTenantSettings(UUID tenantId) {
        TenantSettings defaults = TenantSettings.builder()
                .sessionTimeoutMinutes(DEFAULT_SESSION_TIMEOUT_MINUTES)
                .tenantLogo(null)
                .build();

        if (tenantId == null) {
            log.debug("Tenant ID is null, returning default settings");
            return defaults;
        }

        String url = Objects.requireNonNull(buildTenantEndpoint(tenantId));
        log.info("🔍 Fetching tenant settings from: {}", url);
        try {
            TenantSettings response = restTemplate.getForObject(url, TenantSettings.class);
            log.info("✅ Tenant response received: {}", response);

            if (response == null) {
                log.warn("Tenant response is null, returning defaults");
                return defaults;
            }

            Integer timeout = response.getSessionTimeoutMinutes();
            if (timeout == null || timeout <= 0) {
                log.warn("Invalid timeout {}, using default {}", timeout, DEFAULT_SESSION_TIMEOUT_MINUTES);
                response.setSessionTimeoutMinutes(DEFAULT_SESSION_TIMEOUT_MINUTES);
            }

            log.info("🎯 Final tenant settings: timeout={} minutes, logo={}",
                    response.getSessionTimeoutMinutes(), response.getTenantLogo());
            return response;
        } catch (Exception ex) {
            log.error("❌ Failed to fetch tenant settings for tenant {}: {}", tenantId, ex.getMessage(), ex);
            return defaults;
        }
    }

    private String buildTenantEndpoint(UUID tenantId) {
        StringBuilder base = new StringBuilder();
        if (accessServiceBaseUrl != null && !accessServiceBaseUrl.isBlank()) {
            base.append(accessServiceBaseUrl.trim());
        } else {
            base.append(DEFAULT_ACCESS_SERVICE_BASE_URL);
        }
        while (base.length() > 0 && base.charAt(base.length() - 1) == '/') {
            base.deleteCharAt(base.length() - 1);
        }
        base.append("/tenants/").append(tenantId);
        return base.toString();
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class TenantSettings {
        private Integer sessionTimeoutMinutes;
        private String tenantLogo;
    }


    @PostMapping("/register")
    public JwtResponseDTO register(@Valid @RequestBody RegisterRequestDTO request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.getFirstName(),
                request.getFatherName(),
                request.getSurname(),
                request.getFullName(),
                request.getEmail(),
                request.getPassword(),
                request.getConfirmPassword(),
                request.getType(),
                request.getLanguage()
        );
        User user = registerUserUseCase.register(command);

        String accountKind = user.getAccountKind();
        if (accountKind == null || accountKind.isBlank()) {
            accountKind = request.getType() != null ? request.getType() : "GENERAL";
        }

        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getId().toString(),
                accountKind,
                user.getLanguage()
        );
        return new JwtResponseDTO(token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            log.info("🔐 Login attempt for email: {}", request.getEmail());
            User user = loginUseCase.login(request.getEmail(), request.getPassword());
            log.info("✅ User authenticated: {}", user.getId());
            log.info("   - Tenant ID: {}", user.getTenantId());
            log.info("   - Email: {}", user.getEmail());
            log.info("   - Full Name: {}", user.getFullName());

            String accountKind = user.getAccountKind();
            if (accountKind == null || accountKind.isBlank()) {
                accountKind = "GENERAL";
            }

            String token = jwtTokenProvider.generateToken(
                    user.getId(),
                    user.getId().toString(),
                    accountKind,
                    request.getLanguage()
            );

            // Resolve tenant settings from Access Management Service
            TenantSettings tenantSettings = resolveTenantSettings(user.getTenantId());

            // Return standardized auth response
            AuthSuccessResponse response = AuthSuccessResponse.builder()
                    .accessToken(token)
                    .userId(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .accountKind(accountKind)
                    .language(user.getLanguage())
                    .requiresPasswordChange(Boolean.TRUE.equals(user.getMustChangePassword()) || user.isPasswordExpired())
                    .requiresRenewal(user.needsRenewal())
                    .sessionTimeoutMinutes(tenantSettings.getSessionTimeoutMinutes())
                    .tenantLogo(tenantSettings.getTenantLogo())
                    .build();

            log.info("🎉 Login response prepared: sessionTimeout={}, logo={}",
                    response.getSessionTimeoutMinutes(), response.getTenantLogo());

            return ResponseEntity.ok(response);
            
        } catch (PasswordChangeRequiredException e) {
            log.warn("Password change required for user: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(java.util.Map.of(
                            "error", "PASSWORD_CHANGE_REQUIRED",
                            "message", e.getMessage(),
                            "requiresPasswordChange", true
                    ));
        }
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponseDTO> refreshToken(@RequestBody @Valid RefreshTokenRequestDTO request) {
        String newAccessToken = refreshTokenUseCase.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(new JwtResponseDTO(newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid RefreshTokenRequestDTO request){
        logoutUseCase.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    /**
     * OAuth callback endpoint - handles OAuth authorization code exchange and user login
     */
    @PostMapping("/oauth/callback")
    public ResponseEntity<OAuthLoginResponse> oauthCallback(@Valid @RequestBody OAuthCallbackRequest request) {
        try {
            log.info("Processing OAuth callback for provider: {}", request.getProvider());
            
            OAuthProvider provider = OAuthProvider.fromString(request.getProvider());
            
            if (!oauthService.isConfigured(provider)) {
                log.error("OAuth provider {} is not configured", provider);
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(OAuthLoginResponse.builder()
                        .error("OAuth provider not configured")
                        .message("Please configure OAuth credentials for " + provider)
                        .build());
            }
            
            // Step 1: Exchange authorization code for access token
            log.debug("Exchanging authorization code for access token");
            String accessToken = oauthService.exchangeCodeForToken(
                provider,
                request.getCode(),
                request.getRedirectUri()
            );
            
            // Step 2: Fetch user profile from OAuth provider
            log.debug("Fetching user profile from OAuth provider");
            Map<String, Object> profile = oauthService.getUserProfile(provider, accessToken);
            
            String email = (String) profile.get("email");
            if (email == null || email.isBlank()) {
                log.error("OAuth profile missing email for provider: {}", provider);
                return ResponseEntity.badRequest()
                    .body(OAuthLoginResponse.builder()
                        .error("Email not provided")
                        .message("Your " + provider + " account must have a verified email address")
                        .build());
            }
            
            // Step 3: Find or create user and link provider account
            log.debug("Finding or creating user from OAuth profile");
            UserJpaEntity user = oauthAccountLinkService.findOrCreateUserFromOAuth(provider, profile);
            
            // Step 4: Generate JWT token
            log.debug("Generating JWT token for user: {}", user.getEmail());
            String jwtToken = jwtTokenProvider.generateToken(
                user.getId(),
                user.getId().toString(),
                user.getAccountKind().name(),
                user.getLanguage()
            );

            // Resolve tenant settings from Access Management Service
            TenantSettings tenantSettings = resolveTenantSettings(user.getTenantId());

            // Step 5: Build and return successful response
            OAuthLoginResponse response = OAuthLoginResponse.builder()
                .accessToken(jwtToken)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .newUser(user.getCreatedAt().isAfter(java.time.Instant.now().minusSeconds(5)))
                .provider(provider.name())
                .language(user.getLanguage())
                .sessionTimeoutMinutes(tenantSettings.getSessionTimeoutMinutes())
                .tenantLogo(tenantSettings.getTenantLogo())
                .build();
            
            log.info("OAuth login successful for user: {}, provider: {}", user.getEmail(), provider);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid OAuth request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(OAuthLoginResponse.builder()
                    .error("Invalid request")
                    .message(e.getMessage())
                    .build());
                    
        } catch (RuntimeException e) {
            log.error("OAuth callback failed", e);
            
            if (e.getMessage().contains("disabled") || e.getMessage().contains("deleted")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(OAuthLoginResponse.builder()
                        .error("Account error")
                        .message(e.getMessage())
                        .build());
            }
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(OAuthLoginResponse.builder()
                    .error("OAuth login failed")
                    .message("Unable to complete OAuth login. Please try again.")
                    .build());
                    
        } catch (Exception e) {
            log.error("Unexpected error during OAuth callback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(OAuthLoginResponse.builder()
                    .error("Server error")
                    .message("An unexpected error occurred. Please try again later.")
                    .build());
        }
    }
    
    /**
     * Get OAuth configuration for frontend
     */
    @GetMapping("/oauth/config")
    public ResponseEntity<java.util.Map<String, String>> getOAuthConfig() {
        return ResponseEntity.ok(java.util.Map.of(
            "googleClientId", googleClientId != null && !googleClientId.isBlank() ? googleClientId : "NOT_CONFIGURED",
            "microsoftClientId", microsoftClientId != null && !microsoftClientId.isBlank() ? microsoftClientId : "NOT_CONFIGURED",
            "status", (googleClientId != null && !googleClientId.isBlank()) ? "ready" : "needs_configuration"
        ));
    }

}

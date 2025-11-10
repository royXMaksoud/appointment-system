package com.ftp.authservice.application.service;

import com.ftp.authservice.domain.model.OAuthProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class OAuthService {

    private final RestTemplate restTemplate;

    public OAuthService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Value("${oauth.google.client-id:}")
    private String googleClientId;
    @Value("${oauth.google.client-secret:}")
    private String googleClientSecret;
    @Value("${oauth.google.token-uri:https://oauth2.googleapis.com/token}")
    private String googleTokenUri;
    @Value("${oauth.google.user-info-uri:https://www.googleapis.com/oauth2/v3/userinfo}")
    private String googleUserInfoUri;

    @Value("${oauth.microsoft.client-id:}")
    private String microsoftClientId;
    @Value("${oauth.microsoft.client-secret:}")
    private String microsoftClientSecret;
    @Value("${oauth.microsoft.token-uri:https://login.microsoftonline.com/common/oauth2/v2.0/token}")
    private String microsoftTokenUri;
    @Value("${oauth.microsoft.user-info-uri:https://graph.microsoft.com/v1.0/me}")
    private String microsoftUserInfoUri;

    public boolean isConfigured(OAuthProvider provider) {
        return switch (provider) {
            case GOOGLE -> isNotBlank(googleClientId) && isNotBlank(googleClientSecret);
            case MICROSOFT -> isNotBlank(microsoftClientId) && isNotBlank(microsoftClientSecret);
        };
    }

    public String exchangeCodeForToken(OAuthProvider provider, String code, String redirectUri) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("redirect_uri", redirectUri);
        form.add("grant_type", "authorization_code");

        switch (provider) {
            case GOOGLE -> {
                form.add("client_id", googleClientId);
                form.add("client_secret", googleClientSecret);
            }
            case MICROSOFT -> {
                form.add("client_id", microsoftClientId);
                form.add("client_secret", microsoftClientSecret);
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(form, headers);

        String tokenEndpoint = switch (provider) {
            case GOOGLE -> googleTokenUri;
            case MICROSOFT -> microsoftTokenUri;
        };

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    tokenEndpoint,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<>() {}
            );
            Map<String, Object> body = response.getBody();
            if (body == null || body.get("access_token") == null) {
                throw new IllegalStateException("OAuth provider did not return an access token");
            }
            return Objects.toString(body.get("access_token"));
        } catch (RestClientException ex) {
            log.error("Failed to exchange code for token with provider {}", provider, ex);
            throw new RuntimeException("OAuth token exchange failed: " + ex.getMessage(), ex);
        }
    }

    public Map<String, Object> getUserProfile(OAuthProvider provider, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String userInfoEndpoint = switch (provider) {
            case GOOGLE -> googleUserInfoUri;
            case MICROSOFT -> microsoftUserInfoUri;
        };

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    userInfoEndpoint,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );
            Map<String, Object> body = response.getBody();
            if (body == null) {
                throw new IllegalStateException("OAuth provider returned empty profile");
            }
            return body;
        } catch (RestClientException ex) {
            log.error("Failed to retrieve user profile from provider {}", provider, ex);
            throw new RuntimeException("OAuth profile retrieval failed: " + ex.getMessage(), ex);
        }
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}


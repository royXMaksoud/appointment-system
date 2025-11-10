package com.sharedlib.core.interceptor;

import com.sharedlib.core.context.CurrentUser;
import com.sharedlib.core.context.CurrentUserContext;
import com.sharedlib.core.context.LanguageContext;
import com.sharedlib.core.security.JwtTokenProvider;
import io.jsonwebtoken.Claims; // Added for extracting claims
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.*;

/**
 * ContextInterceptor manages language and user context for each HTTP request.
 *
 * This interceptor:
 * 1. Extracts language from request headers, parameters, or JWT token
 * 2. Extracts user information from JWT token
 * 3. Sets both contexts in ThreadLocal for use throughout the request
 * 4. Cleans up ThreadLocal after request completion to prevent memory leaks
 *
 * @author CARE Team
 * @version 1.0
 * @since 2025-01-27
 */
@Component
@Slf4j
public class ContextInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String language = extractLanguageFromRequest(request);
            LanguageContext.setLanguage(language);
            log.debug("Language context set to: {}", language);

            CurrentUser userInfo = extractUserFromJWT(request);
            if (userInfo != null) {
                CurrentUserContext.set(userInfo);
                log.debug("User context set for user: {}", userInfo.userId());
            } else {
                log.debug("No user context found in request");
            }

            return true;
        } catch (Exception e) {
            log.error("Error in ContextInterceptor preHandle: {}", e.getMessage(), e);
            return true;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            LanguageContext.clear();
            CurrentUserContext.clear();
            log.debug("Context cleanup completed");
        } catch (Exception e) {
            log.error("Error in ContextInterceptor cleanup: {}", e.getMessage(), e);
        }
    }

    /**
     * Extracts language from request.
     * Now uses "lang" claim from JWT if available instead of "language".
     */
    private String extractLanguageFromRequest(HttpServletRequest request) {
        String langParam = request.getParameter("lang");
        if (langParam != null && !langParam.trim().isEmpty()) {
            log.debug("Language extracted from parameter: {}", langParam);
            return langParam.toLowerCase();
        }

        String acceptLanguage = request.getHeader("Accept-Language");
        if (acceptLanguage != null && !acceptLanguage.trim().isEmpty()) {
            String primaryLanguage = extractPrimaryLanguage(acceptLanguage);
            log.debug("Language extracted from Accept-Language header: {}", primaryLanguage);
            return primaryLanguage;
        }

        try {
            String token = extractTokenFromRequest(request);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // Changed "language" to "lang" claim
                String jwtLanguage = jwtTokenProvider.getClaims(token).get("lang", String.class);
                if (jwtLanguage != null) {
                    log.debug("Language extracted from JWT token: {}", jwtLanguage);
                    return jwtLanguage.toLowerCase();
                }
            }
        } catch (Exception e) {
            log.debug("Could not extract language from JWT token: {}", e.getMessage());
        }

        log.debug("Using default language: en");
        return "en";
    }

    private String extractPrimaryLanguage(String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.trim().isEmpty()) {
            return "en";
        }
        String[] languages = acceptLanguage.split(",");
        if (languages.length > 0) {
            String primaryLanguage = languages[0].trim().split(";")[0].split("-")[0];
            return primaryLanguage.toLowerCase();
        }
        return "en";
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Extracts user information from JWT token.
     * Updated to support the new CurrentUser constructor with attributes map.
     */
    private CurrentUser extractUserFromJWT(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null) {
                return null;
            }

            if (!jwtTokenProvider.validateToken(token)) {
                log.debug("Invalid JWT token provided");
                return null;
            }

            Claims claims = jwtTokenProvider.getClaims(token);
            if (claims == null) {
                return null;
            }

            // Prepare attributes map from all claims except standard ones
            Map<String, Object> attributes = new HashMap<>(claims);
            attributes.remove("sub");
            attributes.remove("iat");
            attributes.remove("exp");
            attributes.remove("email");
            attributes.remove("userType");
            attributes.remove("lang"); // Changed from "language"
            attributes.remove("roles");
            attributes.remove("permissions");

            // Pass attributes as the 7th parameter to match CurrentUser record
            CurrentUser userInfo = new CurrentUser(
                    UUID.fromString(claims.getSubject()),
                    claims.get("userType", String.class),
                    claims.get("email", String.class),
                    claims.get("lang", String.class), // Changed from "language"
                    extractRolesFromClaims(claims),
                    extractPermissionsFromClaims(claims),
                    attributes
            );

            return userInfo;
        } catch (Exception e) {
            log.debug("Error extracting user from JWT: {}", e.getMessage());
            return null;
        }
    }

    private List<String> extractRolesFromClaims(io.jsonwebtoken.Claims claims) {
        try {
            Object roles = claims.get("roles");
            if (roles instanceof List<?>) {
                return ((List<?>) roles).stream().map(Object::toString).toList();
            } else if (roles instanceof String) {
                return List.of(((String) roles).split(","));
            }
            return List.of();
        } catch (Exception e) {
            log.debug("Error extracting roles from claims: {}", e.getMessage());
            return List.of();
        }
    }

    private List<String> extractPermissionsFromClaims(io.jsonwebtoken.Claims claims) {
        try {
            Object permissions = claims.get("permissions");
            if (permissions instanceof List<?>) {
                return ((List<?>) permissions).stream().map(Object::toString).toList();
            } else if (permissions instanceof String) {
                return List.of(((String) permissions).split(","));
            }
            return List.of();
        } catch (Exception e) {
            log.debug("Error extracting permissions from claims: {}", e.getMessage());
            return List.of();
        }
    }
}

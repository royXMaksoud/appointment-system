package com.ftp.authservice.domain.model;

import java.util.Locale;

/**
 * Supported OAuth providers for the authentication service.
 */
public enum OAuthProvider {
    GOOGLE,
    MICROSOFT;

    public static OAuthProvider fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OAuth provider must be supplied");
        }
        try {
            return OAuthProvider.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported OAuth provider: " + value);
        }
    }
}


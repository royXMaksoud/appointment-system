package com.sharedlib.core.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Provides utility methods to parse and validate JWT tokens.
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    /**
     * Generates a JWT token with the specified claims.
     */
    public String generateToken(UUID userId, String email, String userType, String language, 
                               List<String> roles, List<String> permissions) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("userType", userType)
                .claim("lang", language)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts the user ID (subject) from the JWT token.
     */
    public UUID getUserId(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    /**
     * Extracts the language code from the JWT token.
     */
    public String getLanguage(String token) {
        return getClaims(token).get("lang", String.class);
    }

    /**
     * Extracts the user type (business concept, e.g., User, Admin) from the JWT token.
     */
    public String getUserType(String token) {
        return getClaims(token).get("userType", String.class);
    }

    /**
     * Extracts the roles from the JWT token as a List<String>.
     * Returns an empty list if not present.
     */
    public java.util.List<String> getRoles(String token) {
        Object roles = getClaims(token).get("roles");
        if (roles instanceof java.util.List<?>) {
            return ((java.util.List<?>) roles).stream()
                    .map(Object::toString)
                    .toList();
        } else if (roles instanceof String) {
            // Support comma-separated string
            return java.util.Arrays.asList(((String) roles).split(","));
        }
        return java.util.Collections.emptyList();
    }

    /**
     * Extracts the email from the JWT token.
     */
    public String getEmail(String token) {
        return getClaims(token).get("email", String.class);
    }

    /**
     * Extracts permissions from the JWT token as a List<String>.
     * Returns an empty list if not present.
     */
    public java.util.List<String> getPermissions(String token) {
        Object perms = getClaims(token).get("permissions");
        if (perms instanceof java.util.List<?>) {
            return ((java.util.List<?>) perms).stream()
                    .map(Object::toString)
                    .toList();
        } else if (perms instanceof String) {
            return java.util.Arrays.asList(((String) perms).split(","));
        }
        return java.util.Collections.emptyList();
    }

    /**
     * Extracts the expiration date from the JWT token.
     */
    public Date getExpirationDate(String token) {
        return getClaims(token).getExpiration();
    }
}

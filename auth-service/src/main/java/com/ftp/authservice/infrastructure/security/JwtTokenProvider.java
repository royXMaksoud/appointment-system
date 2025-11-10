// JwtTokenProvider.java — single source of truth for JWT creation/validation
// Why: previously used two different keys (hardcoded and from properties) and mixed claim names.
// This version always uses the configured secret and a consistent claim name "userType".

package com.ftp.authservice.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component("localJwtTokenProvider")
public class JwtTokenProvider {

    private final Key secretKey;
    private final long jwtExpiration;
    private final long jwtRefreshExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long jwtExpiration,
            @Value("${jwt.refreshExpiration}") long jwtRefreshExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpiration = jwtExpiration;
        this.jwtRefreshExpiration = jwtRefreshExpiration;
    }

    // Generate access token with consistent claims
    public String generateToken(UUID userId, String email, String userType, String language) {
        return buildToken(userId, email, userType, language, jwtExpiration);
    }

    // Generate refresh token if you use refresh flow
    public String generateRefreshToken(UUID userId, String email, String language, String userType) {
        return buildToken(userId, email, userType, language, jwtRefreshExpiration);
    }

    private String buildToken(UUID userId, String email, String userType, String language, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .claim("userType", userType)   // <— standardized (not "usertype")
                .claim("lang", language)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isValidToken(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(parse(token).getBody().getSubject());
    }

    public Date extractExpiration(String token) {
        return parse(token).getBody().getExpiration();
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }
}

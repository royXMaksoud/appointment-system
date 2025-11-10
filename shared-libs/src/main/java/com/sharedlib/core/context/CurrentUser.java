package com.sharedlib.core.context;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents the current authenticated user in the system.
 * This record contains all necessary user information extracted from JWT token.
 * 
 * @author CARE Team
 * @version 1.0
 * @since 2025-01-27
 */
public record CurrentUser(
    UUID userId,
    String userType,
    String language,
    String email,
    List<String> roles,
    List<String> permissions,
    Map<String, Object> claims
) {
    
    /**
     * Creates a CurrentUser with default values for optional fields.
     * 
     * @param userId The unique identifier of the user
     * @param userType The type of user (e.g., "ADMIN", "USER", "TENANT")
     * @param language The user's preferred language
     * @param email The user's email address
     * @param roles List of user roles
     * @param permissions List of user permissions
     */
    public CurrentUser {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (userType == null) {
            userType = "USER";
        }
        if (language == null) {
            language = "en";
        }
        if (email == null) {
            email = "";
        }
        if (roles == null) {
            roles = List.of();
        }
        if (permissions == null) {
            permissions = List.of();
        }
        if (claims == null) claims = Map.of();
    }
    public String getStringClaim(String name) {
        Object v = claims.get(name);
        return v != null ? String.valueOf(v) : null;
    }
    public UUID getUuidClaim(String name) {
        String s = getStringClaim(name);
        try { return s != null ? UUID.fromString(s) : null; }
        catch (IllegalArgumentException e) { return null; }
    }


    /**
     * Checks if the user has a specific role.
     * 
     * @param role The role to check
     * @return true if user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        return roles.contains(role);
    }
    
    /**
     * Checks if the user has a specific permission.
     * 
     * @param permission The permission to check
     * @return true if user has the permission, false otherwise
     */
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
    
    /**
     * Checks if the user is an administrator.
     * 
     * @return true if user has ADMIN role, false otherwise
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
    
    /**
     * Checks if the user is a tenant user.
     * 
     * @return true if user type is TENANT, false otherwise
     */
    public boolean isTenantUser() {
        return "TENANT".equals(userType);
    }
}

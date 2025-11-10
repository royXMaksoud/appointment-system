package com.sharedlib.core.util;

import com.sharedlib.core.context.CurrentUser;
import com.sharedlib.core.context.CurrentUserContext;
import com.sharedlib.core.context.LanguageContext;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Utility class for accessing user context and language information.
 * Provides convenient methods to access current user and language from ThreadLocal context.
 * 
 * This utility class simplifies access to:
 * - Current user information (ID, email, roles, permissions)
 * - Current language setting
 * - User authentication status
 * - Role and permission checks
 * 
 * @author CARE Team
 * @version 1.0
 * @since 2025-01-27
 */
@Slf4j
public class ContextUtil {

    private ContextUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Gets the current user from ThreadLocal context.
     * 
     * @return CurrentUser object or null if not authenticated
     */
    public static CurrentUser getCurrentUser() {
        return CurrentUserContext.get();
    }

    /**
     * Gets the current user ID from ThreadLocal context.
     * 
     * @return UUID of current user or null if not authenticated
     */
    public static UUID getCurrentUserId() {
        return CurrentUserContext.getUserId();
    }

    /**
     * Gets the current user's email from ThreadLocal context.
     * 
     * @return Email of current user or null if not authenticated
     */
    public static String getCurrentUserEmail() {
        return CurrentUserContext.getUserEmail();
    }

    /**
     * Gets the current user's language from ThreadLocal context.
     * 
     * @return Language code (e.g., "en", "ar") or "en" as default
     */
    public static String getCurrentLanguage() {
        return LanguageContext.getLanguage();
    }

    /**
     * Gets the current user's roles from ThreadLocal context.
     * 
     * @return List of user roles or empty list if not authenticated
     */
    public static java.util.List<String> getCurrentUserRoles() {
        CurrentUser currentUser = getCurrentUser();
        return currentUser != null ? currentUser.roles() : java.util.List.of();
    }

    /**
     * Gets the current user's permissions from ThreadLocal context.
     * 
     * @return List of user permissions or empty list if not authenticated
     */
    public static java.util.List<String> getCurrentUserPermissions() {
        CurrentUser currentUser = getCurrentUser();
        return currentUser != null ? currentUser.permissions() : java.util.List.of();
    }

    /**
     * Gets the current user's type from ThreadLocal context.
     * 
     * @return User type (e.g., "ADMIN", "USER", "TENANT") or null if not authenticated
     */
    public static String getCurrentUserType() {
        CurrentUser currentUser = getCurrentUser();
        return currentUser != null ? currentUser.userType() : null;
    }

    /**
     * Checks if the current user has a specific role.
     * 
     * @param role The role to check
     * @return true if user has the role, false otherwise
     */
    public static boolean hasRole(String role) {
        return CurrentUserContext.hasRole(role);
    }

    /**
     * Checks if the current user has a specific permission.
     * 
     * @param permission The permission to check
     * @return true if user has the permission, false otherwise
     */
    public static boolean hasPermission(String permission) {
        return CurrentUserContext.hasPermission(permission);
    }

    /**
     * Checks if the current user is authenticated.
     * 
     * @return true if user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        return CurrentUserContext.hasUser();
    }

    /**
     * Checks if the current language is Arabic.
     * 
     * @return true if language is Arabic, false otherwise
     */
    public static boolean isArabic() {
        return "ar".equals(getCurrentLanguage());
    }

    /**
     * Checks if the current language is right-to-left.
     * 
     * @return true if language is right-to-left (Arabic), false otherwise
     */
    public static boolean isRightToLeft() {
        return isArabic();
    }

    /**
     * Gets the current locale string.
     * 
     * @return Locale string (e.g., "en_US", "ar_SA")
     */
    public static String getCurrentLocale() {
        String language = getCurrentLanguage();
        String country = switch (language) {
            case "ar" -> "SA";
            case "en" -> "US";
            default -> "US";
        };
        return language + "_" + country;
    }

    /**
     * Checks if the current user is an administrator.
     * 
     * @return true if user has ADMIN role, false otherwise
     */
    public static boolean isAdmin() {
        return CurrentUserContext.isAdmin();
    }

    /**
     * Checks if the current user is a tenant user.
     * 
     * @return true if user type is TENANT, false otherwise
     */
    public static boolean isTenantUser() {
        CurrentUser currentUser = getCurrentUser();
        return currentUser != null && currentUser.isTenantUser();
    }

    /**
     * Logs the current context information for debugging purposes.
     */
    public static void logCurrentContext() {
        CurrentUser currentUser = getCurrentUser();
        String language = getCurrentLanguage();
        
        if (currentUser != null) {
            log.debug("Current Context - User: {}, Email: {}, Language: {}, Roles: {}",
                currentUser.userId(), currentUser.email(), language, currentUser.roles());
        } else {
            log.debug("Current Context - No authenticated user, Language: {}", language);
        }
    }

    /**
     * Validates that the current user is authenticated.
     * 
     * @throws IllegalStateException if user is not authenticated
     */
    public static void requireAuthentication() {
        if (!isAuthenticated()) {
            throw new IllegalStateException("User must be authenticated");
        }
    }

    /**
     * Validates that the current user has a specific role.
     * 
     * @param role The required role
     * @throws IllegalStateException if user doesn't have the role
     */
    public static void requireRole(String role) {
        requireAuthentication();
        if (!hasRole(role)) {
            throw new IllegalStateException("User must have role: " + role);
        }
    }

    /**
     * Validates that the current user has a specific permission.
     * 
     * @param permission The required permission
     * @throws IllegalStateException if user doesn't have the permission
     */
    public static void requirePermission(String permission) {
        requireAuthentication();
        if (!hasPermission(permission)) {
            throw new IllegalStateException("User must have permission: " + permission);
        }
    }
} 
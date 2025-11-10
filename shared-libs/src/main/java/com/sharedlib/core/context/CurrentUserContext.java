package com.sharedlib.core.context;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * ThreadLocal context holder for the current authenticated user.
 * 
 * This class provides a thread-safe way to store and retrieve the current user
 * information throughout the request lifecycle. The context is automatically
 * cleaned up after each request to prevent memory leaks.
 * 
 * Usage:
 * - Set user context: CurrentUserContext.set(currentUser)
 * - Get user context: CurrentUser user = CurrentUserContext.get()
 * - Clear context: CurrentUserContext.clear()
 * 
 * @author CARE Team
 * @version 1.0
 * @since 2025-01-27
 */
@Slf4j
public class CurrentUserContext {
    
    private static final ThreadLocal<CurrentUser> currentUser = new ThreadLocal<>();

    private CurrentUserContext() {
        // Private constructor to prevent instantiation
    }

    /**
     * Sets the current user in the ThreadLocal context.
     * 
     * @param user The current user to set
     */
    public static void set(CurrentUser user) {
        if (user == null) {
            log.warn("Attempting to set null user in CurrentUserContext");
            return;
        }
        currentUser.set(user);
        log.debug("Current user context set for user: {}", user.userId());
    }

    /**
     * Gets the current user from the ThreadLocal context.
     * 
     * @return The current user or null if not set
     */
    public static CurrentUser get() {
        CurrentUser user = currentUser.get();
        if (user == null) {
            log.debug("No current user found in context");
        }
        return user;
    }

    /**
     * Gets the current user ID from the ThreadLocal context.
     * 
     * @return The current user ID or null if not set
     */
    public static UUID getUserId() {
        CurrentUser user = get();
        return user != null ? user.userId() : null;
    }

    /**
     * Gets the current user's email from the ThreadLocal context.
     * 
     * @return The current user's email or null if not set
     */
    public static String getUserEmail() {
        CurrentUser user = get();
        return user != null ? user.email() : null;
    }

    /**
     * Gets the current user's language from the ThreadLocal context.
     * 
     * @return The current user's language or "en" as default
     */
    public static String getUserLanguage() {
        CurrentUser user = get();
        return user != null ? user.language() : "en";
    }

    /**
     * Checks if there is a current user in the context.
     * 
     * @return true if a user is set, false otherwise
     */
    public static boolean hasUser() {
        return get() != null;
    }

    /**
     * Clears the current user from the ThreadLocal context.
     * This should be called after each request to prevent memory leaks.
     */
    public static void clear() {
        CurrentUser user = currentUser.get();
        if (user != null) {
            log.debug("Clearing current user context for user: {}", user.userId());
        }
        currentUser.remove();
    }

    /**
     * Checks if the current user has a specific role.
     * 
     * @param role The role to check
     * @return true if user has the role, false otherwise
     */
    public static boolean hasRole(String role) {
        CurrentUser user = get();
        return user != null && user.hasRole(role);
    }

    /**
     * Checks if the current user has a specific permission.
     * 
     * @param permission The permission to check
     * @return true if user has the permission, false otherwise
     */
    public static boolean hasPermission(String permission) {
        CurrentUser user = get();
        return user != null && user.hasPermission(permission);
    }

    /**
     * Checks if the current user is an administrator.
     * 
     * @return true if user has ADMIN role, false otherwise
     */
    public static boolean isAdmin() {
        CurrentUser user = get();
        return user != null && user.isAdmin();
    }
}

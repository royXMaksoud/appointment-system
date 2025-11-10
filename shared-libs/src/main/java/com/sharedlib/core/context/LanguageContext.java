

package com.sharedlib.core.context;

/**
 * Thread-local context for storing the current request language.
 * This allows access to the preferred language of the user across the entire request lifecycle.
 * 
 * Supports both naming conventions for backward compatibility:
 * - setLanguage()/getLanguage() (core-shared-lib style)
 * - set()/get() (appointment-service style)
 */
public class LanguageContext {

    private static final ThreadLocal<String> currentLang = new ThreadLocal<>();

    /**
     * Sets the language for the current thread/request.
     * This is typically extracted from the JWT token and used throughout the request.
     *
     * @param lang the preferred language code (e.g., "en", "ar")
     */
    public static void setLanguage(String lang) {
        currentLang.set(lang);
    }

    /**
     * Gets the language stored for the current thread/request.
     *
     * @return the language code, or "en" as default if not set
     */
    public static String getLanguage() {
        return currentLang.get() != null ? currentLang.get() : "en";
    }

    /**
     * Clears the language from the current thread to avoid memory leaks.
     * This should always be called at the end of the request (e.g., in a filter's finally block).
     */
    public static void clear() {
        currentLang.remove();
    }

    // Backward compatibility methods for appointment-service style
    /**
     * Sets the language for the current thread/request.
     * Alias for setLanguage() for backward compatibility.
     *
     * @param lang the preferred language code (e.g., "en", "ar")
     */
    public static void set(String lang) {
        setLanguage(lang);
    }

    /**
     * Gets the language stored for the current thread/request.
     * Alias for getLanguage() for backward compatibility.
     *
     * @return the language code, or null if not set
     */
    public static String get() {
        return currentLang.get();
    }
}

package com.sharedlib.core.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard structure for all error responses returned to clients.
 * It ensures consistency and supports internationalized error messages.
 */
@Getter
@Builder
public class ErrorResponse {

    private final String code;          // Error code or i18n key
    private final String message;       // Localized error message
    private final int status;           // HTTP status code
    private final LocalDateTime timestamp; // Time when error occurred
    private final String path;          // Request path where error happened
    private final List<ValidationError> details; // Field-specific validation errors
    
    /**
     * Represents a field-specific validation error.
     */
    @Getter
    @Builder
    public static class ValidationError {
        private final String field;     // Field name that failed validation
        private final String message;   // Localized error message for this field
        private final String code;      // Error code for this specific field
    }
}

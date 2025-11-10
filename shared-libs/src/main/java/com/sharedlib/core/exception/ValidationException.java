package com.sharedlib.core.exception;

import com.sharedlib.core.dto.ErrorResponse;
import lombok.Getter;

import java.util.List;

/**
 * Exception thrown when validation fails.
 * Supports both single error messages and multiple validation errors.
 */
@Getter
public class ValidationException extends RuntimeException {

    private final List<String> errors;
    private final List<ErrorResponse.ValidationError> validationErrors;
    /**
     * -- GETTER --
     * i18n key used by handlers/resolvers.
     */
    private final String messageKey;

    /** Single message (also used as messageKey). */
    public ValidationException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
        this.errors = List.of(messageKey);
        this.validationErrors = null;
    }

    /** Multiple plain error messages. */
    public ValidationException(List<String> errors) {
        super("Validation failed: " + String.join(", ", errors));
        this.messageKey = "error.validation";
        this.errors = errors;
        this.validationErrors = null;
    }

    /** Field-level errors with messageKey (message == messageKey). */
    public ValidationException(String messageKey, List<ErrorResponse.ValidationError> validationErrors) {
        super(messageKey);
        this.messageKey = messageKey;
        this.errors = null;
        this.validationErrors = validationErrors;
    }

    /** Field-level errors with explicit message (separate from messageKey). */
    public ValidationException(String messageKey, String message, List<ErrorResponse.ValidationError> validationErrors) {
        super(message != null && !message.isBlank() ? message : messageKey);
        this.messageKey = messageKey;
        this.errors = null;
        this.validationErrors = validationErrors;
    }

    /** Factory: field errors + messageKey (message == messageKey). */
    public static ValidationException withFieldErrors(String messageKey,
                                                      List<ErrorResponse.ValidationError> errors) {
        return new ValidationException(messageKey, errors);
    }

    /** Factory: field errors + messageKey + explicit message. */
    public static ValidationException withFieldErrors(String messageKey, String message,
                                                      List<ErrorResponse.ValidationError> errors) {
        return new ValidationException(messageKey, message, errors);
    }

    /** Whether field-specific errors are present. */
    public boolean hasFieldErrors() {
        return validationErrors != null && !validationErrors.isEmpty();
    }

    /** Returns field-specific validation errors (never null). */
    public List<ErrorResponse.ValidationError> getFieldErrors() {
        return validationErrors != null ? validationErrors : List.of();
    }

}

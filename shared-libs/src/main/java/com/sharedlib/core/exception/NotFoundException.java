package com.sharedlib.core.exception;

/**
 * Thrown when a requested resource is not found.
 * Commonly used when an entity is missing in the database.
 */
public class NotFoundException extends RuntimeException {
    /**
     * Error code for i18n message resolution.
     */
    private final String errorCode;

    /**
     * Constructs a NotFoundException with a message.
     * @param message the error message
     */
    public NotFoundException(String message) {
        super(message);
        this.errorCode = "error.notfound";
    }

    /**
     * Constructs a NotFoundException with a message and cause.
     * @param message the error message
     * @param cause the cause of the exception
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "error.notfound";
    }

    /**
     * Returns the error code for i18n message resolution.
     * @return error code
     */
    public String getErrorCode() {
        return errorCode;
    }
}

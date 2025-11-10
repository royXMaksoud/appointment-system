package com.sharedlib.core.exception;

import java.io.Serializable;

/**
 * Thrown when a bad request is made by the client.
 * Typically used for invalid input or malformed data.
 */
public class BadRequestException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Error code for i18n message resolution.
     */
    private final String errorCode;

    /**
     * Constructs a BadRequestException with a message.
     * @param message the error message
     */
    public BadRequestException(String message) {
        super(message);
        this.errorCode = "error.badrequest";
    }

    /**
     * Constructs a BadRequestException with a message and cause.
     * @param message the error message
     * @param cause the cause of the exception
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "error.badrequest";
    }

    /**
     * Returns the error code for i18n message resolution.
     * @return error code
     */
    public String getErrorCode() {
        return errorCode;
    }
}

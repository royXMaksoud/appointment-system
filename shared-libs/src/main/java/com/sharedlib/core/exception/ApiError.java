package com.sharedlib.core.exception;

import java.io.Serializable;

/**
 * Represents a structured error object with code and message.
 * Used for API error responses.
 */
public class ApiError implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Error code representing the type of error.
     */
    private final String code;

    /**
     * Human-readable error message.
     */
    private final String message;

    /**
     * Constructs an ApiError with the specified code and message.
     * @param code the error code
     * @param message the error message
     */
    public ApiError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Returns the error code.
     * @return error code
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the error message.
     * @return error message
     */
    public String getMessage() {
        return message;
    }
}

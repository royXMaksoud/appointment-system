package com.sharedlib.core.exception;

import com.sharedlib.core.constants.ErrorCodes;

/**
 * Thrown when a resource conflict occurs (HTTP 409).
 */
public class ConflictException extends MessageResolvableException {
    public ConflictException(String message) {
        super(ErrorCodes.CONFLICT, message);
    }
    public ConflictException(String message, Throwable cause) {
        super(ErrorCodes.CONFLICT, cause, message);
    }
}

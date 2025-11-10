package com.sharedlib.core.exception;

import com.sharedlib.core.constants.ErrorCodes;

/**
 * Thrown when access to a resource is forbidden (HTTP 403).
 */
public class ForbiddenException extends MessageResolvableException {
    public ForbiddenException(String message) {
        super(ErrorCodes.FORBIDDEN, message);
    }
    public ForbiddenException(String message, Throwable cause) {
        super(ErrorCodes.FORBIDDEN, cause, message);
    }
}

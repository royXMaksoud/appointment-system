package com.ftp.authservice.exception;

/**
 * Raised when the user must change their password before continuing to use the
 * system (expired password, forced rotation, etc.).
 */
public class PasswordChangeRequiredException extends RuntimeException {

    public PasswordChangeRequiredException(String message) {
        super(message);
    }

    public PasswordChangeRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
}


package com.ftp.authservice.exception;



import com.sharedlib.core.exception.MessageResolvableException;

public class UserAlreadyExistsException extends MessageResolvableException {
    public UserAlreadyExistsException(String email) {
        super("user.email.duplicate", new Object[]{ email });
    }
}
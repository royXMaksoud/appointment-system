package com.ftp.authservice.exception;

import com.sharedlib.core.exception.BadRequestException;

public class InvalidCredentialsException extends BadRequestException {
    public InvalidCredentialsException() { super("error.login.invalid"); }
}

package com.ftp.authservice.domain.ports.in;

import com.ftp.authservice.application.command.RegisterUserCommand;
import com.ftp.authservice.domain.model.User;

public interface RegisterUserUseCase {
    User register(RegisterUserCommand command);

}

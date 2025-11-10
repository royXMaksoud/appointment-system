package com.ftp.authservice.domain.ports.in.user;

import com.ftp.authservice.application.user.command.CreateUserCommand;
import com.ftp.authservice.domain.model.User;


public interface SaveUseCase
        extends com.sharedlib.core.domain.ports.in.CreateUseCase<CreateUserCommand, User> {

    default User saveUser(CreateUserCommand command) {
        return create(command);
    }
}

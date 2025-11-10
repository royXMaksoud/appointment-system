package com.ftp.authservice.domain.ports.in.user;

import com.ftp.authservice.application.user.command.UpdateUserCommand;
import com.ftp.authservice.domain.model.User;


import java.util.UUID;



public interface UpdateUseCase
        extends com.sharedlib.core.domain.ports.in.UpdateUseCase<UUID, UpdateUserCommand, User> {

    default User updateUser(UpdateUserCommand command) {
        return update(command.getUserId(), command);
    }
}

package com.ftp.authservice.domain.ports.in.user;



import com.ftp.authservice.domain.model.User;

import java.util.Optional;
import java.util.UUID;


public interface LoadUseCase
        extends com.sharedlib.core.domain.ports.in.GetByIdUseCase<UUID, User> {

    default Optional<User> getUserById(UUID id) {
        return Optional.ofNullable(getById(id));
    }
}

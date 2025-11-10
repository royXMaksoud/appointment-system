package com.ftp.authservice.domain.ports.in.user;

import java.util.UUID;



public interface DeleteUseCase
        extends com.sharedlib.core.domain.ports.in.DeleteUseCase<UUID> {

    default void deleteUser(UUID id) {
        delete(id);
    }
}

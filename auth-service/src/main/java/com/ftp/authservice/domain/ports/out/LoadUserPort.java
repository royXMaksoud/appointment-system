package com.ftp.authservice.domain.ports.out;

import com.ftp.authservice.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface LoadUserPort {
    Optional<User> loadUserByEmail(String email);
    Optional<User> loadUserById(UUID id);

}

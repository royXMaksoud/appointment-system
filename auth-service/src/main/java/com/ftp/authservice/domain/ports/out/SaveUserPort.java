package com.ftp.authservice.domain.ports.out;

import com.ftp.authservice.domain.model.User;

public interface SaveUserPort {
    User saveUser(User user);
}

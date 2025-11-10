package com.ftp.authservice.domain.ports.in;

import com.ftp.authservice.domain.model.User;

public interface LoginUseCase {
    User login(String eamil, String password);
}

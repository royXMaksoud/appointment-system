package com.ftp.authservice.domain.ports.in;

public interface LogoutUseCase {
    void logout(String refreshToken);
}

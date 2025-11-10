package com.ftp.authservice.domain.ports.out;

// domain.ports.out.DeleteRefreshTokenPort
public interface DeleteRefreshTokenPort {
    void deleteByToken(String token);
}

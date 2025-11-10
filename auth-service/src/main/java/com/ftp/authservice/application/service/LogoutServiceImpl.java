package com.ftp.authservice.application.service;

import com.ftp.authservice.domain.ports.in.LogoutUseCase;
import com.ftp.authservice.domain.ports.out.DeleteRefreshTokenPort;
import com.ftp.authservice.infrastructure.db.repositories.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class LogoutServiceImpl implements LogoutUseCase {
    private DeleteRefreshTokenPort deleteRefreshTokenPort;
    public  LogoutServiceImpl(DeleteRefreshTokenPort  deleteRefreshTokenPort) {
        this.deleteRefreshTokenPort  = deleteRefreshTokenPort;
    }
    @Override
    public void logout(String refreshToken) {
        deleteRefreshTokenPort.deleteByToken(refreshToken);

    }
}

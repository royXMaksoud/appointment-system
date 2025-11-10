package com.ftp.authservice.application.service;

import com.ftp.authservice.domain.ports.in.RefreshTokenUseCase;
import com.ftp.authservice.infrastructure.db.entities.RefreshTokenJpaEntity;
import com.ftp.authservice.infrastructure.db.entities.UserJpaEntity;
import com.ftp.authservice.infrastructure.db.repositories.RefreshTokenRepository;
import com.ftp.authservice.infrastructure.db.repositories.UserRepository;
import com.ftp.authservice.infrastructure.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public RefreshTokenServiceImpl(
            RefreshTokenRepository refreshTokenRepository,
            JwtTokenProvider jwtTokenProvider,
            UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        RefreshTokenJpaEntity tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (!tokenEntity.isValid()) {
            throw new RuntimeException("Refresh token expired");
        }

        UserJpaEntity user = tokenEntity.getUser();

        return jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getLanguage() != null ? user.getLanguage() : "en",
                user.getType() != null ? user.getType() : "USER"
        );
    }

}

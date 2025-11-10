package com.ftp.authservice.domain.ports.in;

public interface RefreshTokenUseCase {

    // Generate a new access token from valid refresh token
    String refreshAccessToken(String refreshToken);
}

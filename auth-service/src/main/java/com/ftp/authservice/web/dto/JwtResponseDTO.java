package com.ftp.authservice.web.dto;

public class JwtResponseDTO {
    private String accessToken;

    public JwtResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}

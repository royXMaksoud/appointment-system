package com.ftp.authservice.infrastructure.adapters;

import com.ftp.authservice.domain.ports.out.DeleteRefreshTokenPort;
import com.ftp.authservice.infrastructure.db.repositories.RefreshTokenRepository;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenAdapter implements DeleteRefreshTokenPort {

    private final RefreshTokenRepository repository;

    public RefreshTokenAdapter(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteByToken(String token) {
        repository.deleteByToken(token);
    }
}

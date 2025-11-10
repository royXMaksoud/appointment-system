package com.ftp.authservice.application.service;

import com.ftp.authservice.domain.model.User;
import com.ftp.authservice.domain.ports.in.LoginUseCase;
import com.ftp.authservice.domain.ports.out.LoadUserPort;
import com.ftp.authservice.exception.PasswordChangeRequiredException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginServiceImpl implements LoginUseCase {

    private final LoadUserPort loadUserPort;
    private final PasswordEncoder passwordEncoder;

    public LoginServiceImpl(LoadUserPort loadUserPort, PasswordEncoder passwordEncoder) {
        this.loadUserPort = loadUserPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @CircuitBreaker(name = "authService", fallbackMethod = "loginFallback")
    @Retry(name = "authService")
    @RateLimiter(name = "publicEndpoint")
    @Bulkhead(name = "authService")
    public User login(String email, String rawPassword) {
        log.info("Login attempt for email: {}", email);
        
        User user = loadUserPort.loadUserByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login failed - user not found: {}", email);
                    throw new RuntimeException("Invalid credentials");
                });

        // ✅ Compare hashed password correctly
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            log.warn("Login failed - invalid password for user: {}", email);
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.isEnabled()) {
            log.warn("Login failed - account disabled for user: {}", email);
            throw new RuntimeException("User account is disabled");
        }

        // Check if password change is required
        if (user.isPasswordExpired()) {
            log.warn("Login blocked - password expired for user: {}", email);
            throw new PasswordChangeRequiredException("Password has expired. Please change your password.");
        }

        if (user.getMustChangePassword() != null && user.getMustChangePassword()) {
            log.warn("Login blocked - password change required for user: {}", email);
            throw new PasswordChangeRequiredException("Password change is required.");
        }

        log.info("Login successful for user: {}", email);
        return user;
    }

    /**
     * Fallback method for login failures
     * This is called when circuit breaker opens or other resilience patterns trigger
     */
    @SuppressWarnings("unused")
    private User loginFallback(String email, String rawPassword, Exception ex) {
        log.error("Login fallback triggered for user: {} - Reason: {}", email, ex.getMessage());
        throw new RuntimeException("Authentication service temporarily unavailable. Please try again later.");
    }
}

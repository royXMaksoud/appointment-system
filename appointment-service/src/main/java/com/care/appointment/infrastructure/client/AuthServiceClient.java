package com.care.appointment.infrastructure.client;

import com.care.appointment.infrastructure.client.dto.UserSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 * Feign client for interacting with the auth-service user directory.
 */
@FeignClient(
        name = "auth-service",
        url = "${services.auth.base-url}",
        configuration = FeignClientConfiguration.class
)
public interface AuthServiceClient {

    @GetMapping("/api/users/{userId}")
    UserSummary getUserById(@PathVariable("userId") UUID userId);

}


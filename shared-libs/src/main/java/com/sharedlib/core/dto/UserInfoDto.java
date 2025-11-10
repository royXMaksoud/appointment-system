package com.sharedlib.core.dto;

import java.util.List;
import java.util.UUID;

/**
 * DTO representing user identity and profile details.
 */
public record UserInfoDto(
        UUID userId,
        String username,
        String email,
        String language,
        List<String> roles
) {}

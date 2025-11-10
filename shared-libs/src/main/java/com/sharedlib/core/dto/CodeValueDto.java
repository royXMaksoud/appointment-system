package com.sharedlib.core.dto;

import java.util.UUID;

/**
 * DTO for sharing code table values like status, country, type...
 */
public record CodeValueDto(
        UUID valueId,
        String code,
        String name,
        String language
) {}

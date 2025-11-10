package com.ftp.authservice.application.dto.permissions;


import java.util.List;

/**
 * Minimal Page model to deserialize Access-Management pagination responses.
 */
public record PageResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int number,
        int size,
        boolean last,
        boolean first
) {}

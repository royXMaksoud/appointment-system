package com.sharedlib.core.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * Generic option item for dropdowns (value/label + optional meta).
 * Keep this class small/lightweight because it is used by dropdown endpoints.
 *
 * Example:
 *   new OptionDto<>(uuid, "System A", Map.of("code", "SYS_A"));
 */
@Getter
@AllArgsConstructor
public class OptionDto<T> {
    /** The unique identifier for the option (UUID, String, Long, etc.). */
    private final T value;

    /** The human-readable label shown to the user (should be localized if applicable). */
    private final String label;

    /** Optional extra attributes (e.g., code, icon URL, etc.). */
    private final Map<String, Object> meta;
}

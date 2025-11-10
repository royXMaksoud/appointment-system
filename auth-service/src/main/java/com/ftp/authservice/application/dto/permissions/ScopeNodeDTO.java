package com.ftp.authservice.application.dto.permissions;

import java.util.UUID;

/**
 * Represents a scope node with permission effect
 * Used for hierarchical scope-based permissions
 */
public record ScopeNodeDTO(
        UUID scopeValueId,
        String scopeValueName,
        String effect,  // ALLOW, DENY, NONE
        Integer levelIndex,
        UUID codeTableId,
        String tableName
) {}


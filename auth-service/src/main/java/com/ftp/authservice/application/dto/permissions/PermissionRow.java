package com.ftp.authservice.application.dto.permissions;


import java.util.UUID;

/**
 * One row from the Access-Management "view" endpoint.
 * Keep names/types aligned with that API to avoid mapping overhead.
 * Updated to support new permission system with effects and scopes
 */
public record PermissionRow(
        UUID userId,
        String userName,
        String email,
        UUID systemId,
        String systemName,
        UUID systemSectionId,
        String systemSectionName,
        String system_icon,
        UUID systemSectionActionId,
        String actionName,
        String actionCode,
        String effect,           // ALLOW, DENY, NONE - added for new system
        UUID codeTableId,
        String tableName,
        Integer levelIndex,      // Scope hierarchy level - added
        UUID scopeValueId,       // Changed from String to UUID
        String scopeValueName,
        String permissionType    // ACTION or SCOPE - added
) {}

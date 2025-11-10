package com.ftp.authservice.application.dto.permissions;

import java.util.List;
import java.util.UUID;

/** 
 * Action with permission details
 * Includes effect for action-level permissions and scopes for scope-based permissions
 */
public record ActionDTO(
        UUID systemSectionActionId, 
        String code, 
        String name,
        String effect,              // ALLOW/DENY/NONE for actions without scopes
        List<ScopeNodeDTO> scopes   // List of scopes with their effects
) {}

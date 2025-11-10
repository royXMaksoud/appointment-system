package com.ftp.authservice.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Example spring event listener to evict user permissions cache
 * when Access-Management notifies a change.
 * Wire it to your messaging/event bus if used.
 */
@Component
@RequiredArgsConstructor
public class PermissionCacheEvictionListener {

    private final PermissionAggregationService aggregationService;

    @EventListener
    public void onUserPermissionsChanged(UUID userId) {
        aggregationService.evictUserPermissions(userId);
    }
}

package com.ftp.authservice.web.controller;

import com.ftp.authservice.application.service.PermissionAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Internal endpoints used by Access-Management service to invalidate
 * or refresh cached permission trees after updates.
 */
@RestController
@RequestMapping("/auth/internal/permissions")
@RequiredArgsConstructor
public class PermissionAdminController {

    private final PermissionAggregationService aggregationService;

    @PostMapping("/refresh/{userId}")
    public ResponseEntity<Void> refreshUserPermissions(@PathVariable UUID userId) {
        aggregationService.reloadUserTree(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/evict/{userId}")
    public ResponseEntity<Void> evictUserPermissions(@PathVariable UUID userId) {
        aggregationService.evictUserPermissions(userId);
        return ResponseEntity.noContent().build();
    }
}



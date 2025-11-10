package com.ftp.authservice.web.controller;

import com.ftp.authservice.application.dto.permissions.PermissionTree;
import com.ftp.authservice.application.service.PermissionAggregationService;
import com.ftp.authservice.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/auth/me")
@RequiredArgsConstructor
public class MeController {

    private final PermissionAggregationService permissionAggregationService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/permissions")
    public ResponseEntity<PermissionTree> myPermissions(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatchHeader,
            @RequestParam(value = "force", required = false, defaultValue = "false") boolean force
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        String token = authHeader.substring("Bearer ".length()).trim();
        UUID userId = jwtTokenProvider.getUserIdFromToken(token);

        PermissionTree tree = force
                ? permissionAggregationService.reloadUserTree(userId)
                : permissionAggregationService.loadAndCacheUserTree(userId);

        if (!force && ifNoneMatchHeader != null && !ifNoneMatchHeader.isBlank()) {
            String tag = ifNoneMatchHeader.replace("\"", "").trim();
            if (Objects.equals(tag, tree.etag())) {
                return ResponseEntity.status(304).eTag(tree.etag()).build();
            }
        }
        return ResponseEntity.ok().eTag(tree.etag()).body(tree);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() { return ResponseEntity.ok("me-ok"); }
}

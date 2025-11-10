package com.ftp.authservice.application.service;

import com.ftp.authservice.application.dto.permissions.*;
import com.ftp.authservice.infrastructure.client.PermissionClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionAggregationService {

    private final PermissionClient client;

    @Cacheable(cacheNames = "permissions", key = "#userId")
    public PermissionTree loadAndCacheUserTree(UUID userId) {
        return buildTree(userId);
    }

    @CachePut(cacheNames = "permissions", key = "#userId")
    public PermissionTree reloadUserTree(UUID userId) {
        return buildTree(userId);
    }

    @CacheEvict(cacheNames = "permissions", key = "#userId")
    public void evictUserPermissions(UUID userId) { }

    public boolean hasAnyAccess(UUID userId) {
        try {
            var page = client.fetchUserPermissionsPage(userId, 0, 1, null, null, null);
            return page != null && page.content() != null && !page.content().isEmpty();
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "permission service error", ex);
        }
    }

    private PermissionTree buildTree(UUID userId) {
        var first = client.fetchUserPermissionsPage(userId, 0, 1000, null, null, null);
        List<PermissionRow> all = new ArrayList<>(first.content());
        for (int p = 1; p < first.totalPages(); p++) {
            var next = client.fetchUserPermissionsPage(userId, p, 1000, null, null, null);
            all.addAll(next.content());
        }
        List<SystemDTO> systems = toTree(all);
        String etag = computeEtag(all);
        return new PermissionTree(etag, Instant.now(), systems);
    }

    private static List<SystemDTO> toTree(List<PermissionRow> rows) {
        Map<UUID, Map<UUID, List<PermissionRow>>> bySystemThenSection =
                rows.stream().collect(Collectors.groupingBy(
                        PermissionRow::systemId,
                        Collectors.groupingBy(PermissionRow::systemSectionId)
                ));

        List<SystemDTO> systems = new ArrayList<>();
        for (var sysEntry : bySystemThenSection.entrySet()) {
            UUID systemId = sysEntry.getKey();
            String systemName = rows.stream().filter(r -> r.systemId().equals(systemId))
                    .map(PermissionRow::systemName).filter(Objects::nonNull).findFirst().orElse(null);

            List<SectionDTO> sections = new ArrayList<>();
            for (var secEntry : sysEntry.getValue().entrySet()) {
                UUID sectionId = secEntry.getKey();
                String sectionName = secEntry.getValue().stream()
                        .map(PermissionRow::systemSectionName).filter(Objects::nonNull).findFirst().orElse(null);

                // Group by action, then build ActionDTO with scopes
                Map<UUID, List<PermissionRow>> byAction = secEntry.getValue().stream()
                        .collect(Collectors.groupingBy(PermissionRow::systemSectionActionId));

                List<ActionDTO> actions = new ArrayList<>();
                for (var actionEntry : byAction.entrySet()) {
                    UUID actionId = actionEntry.getKey();
                    List<PermissionRow> actionRows = actionEntry.getValue();
                    
                    // Get action basic info
                    PermissionRow first = actionRows.get(0);
                    String actionCode = first.actionCode();
                    String actionName = first.actionName();
                    
                    // Determine effect and scopes based on permission type
                    String effect = "NONE";
                    List<ScopeNodeDTO> scopes = new ArrayList<>();
                    
                    // Get action-level effect if exists
                    effect = actionRows.stream()
                            .filter(r -> "ACTION".equals(r.permissionType()))
                            .map(PermissionRow::effect)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse("NONE");
                    
                    // Get scope-level permissions (can coexist with action-level)
                    scopes = actionRows.stream()
                            .filter(r -> "SCOPE".equals(r.permissionType()))
                            .filter(r -> r.scopeValueId() != null)
                            .map(r -> new ScopeNodeDTO(
                                    r.scopeValueId(),
                                    r.scopeValueName(),
                                    r.effect(),
                                    r.levelIndex(),
                                    r.codeTableId(),
                                    r.tableName()
                            ))
                            .distinct()
                            .sorted(Comparator.comparing(ScopeNodeDTO::levelIndex, Comparator.nullsFirst(Integer::compareTo))
                                    .thenComparing(ScopeNodeDTO::scopeValueName, Comparator.nullsLast(String::compareToIgnoreCase)))
                            .toList();
                    
                    actions.add(new ActionDTO(actionId, actionCode, actionName, effect, scopes));
                }

                actions.sort(Comparator.comparing(ActionDTO::name, Comparator.nullsLast(String::compareToIgnoreCase)));
                sections.add(new SectionDTO(sectionId, sectionName, actions));
            }
            sections.sort(Comparator.comparing(SectionDTO::name, Comparator.nullsLast(String::compareToIgnoreCase)));
            systems.add(new SystemDTO(systemId, systemName, sections));
        }
        systems.sort(Comparator.comparing(SystemDTO::name, Comparator.nullsLast(String::compareToIgnoreCase)));
        return systems;
    }

    private static String computeEtag(List<PermissionRow> rows) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            for (PermissionRow r : rows) {
                md.update(Objects.toString(r.systemId(), "").getBytes(StandardCharsets.UTF_8));
                md.update(Objects.toString(r.systemSectionId(), "").getBytes(StandardCharsets.UTF_8));
                md.update(Objects.toString(r.systemSectionActionId(), "").getBytes(StandardCharsets.UTF_8));
                md.update(Objects.toString(r.actionCode(), "").getBytes(StandardCharsets.UTF_8));
                md.update(Objects.toString(r.effect(), "").getBytes(StandardCharsets.UTF_8));
                md.update(Objects.toString(r.scopeValueId(), "").getBytes(StandardCharsets.UTF_8));
                md.update(Objects.toString(r.permissionType(), "").getBytes(StandardCharsets.UTF_8));
            }
            byte[] hash = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return UUID.randomUUID().toString();
        }
    }
}

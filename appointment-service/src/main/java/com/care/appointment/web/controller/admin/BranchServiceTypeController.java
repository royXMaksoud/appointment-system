package com.care.appointment.web.controller.admin;

import com.care.appointment.application.branchservice.BranchServiceTypeService;
import com.care.appointment.web.dto.admin.branchservice.BranchServiceTypeDetailDTO;
import com.care.appointment.web.dto.admin.branchservice.BranchServiceTypeSummaryDTO;
import com.care.appointment.web.dto.admin.branchservice.BranchServiceTypeUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
@RestController
@RequestMapping("/api/admin/branch-service-types")
@RequiredArgsConstructor
@Slf4j
public class BranchServiceTypeController {

    private final BranchServiceTypeService branchServiceTypeService;

    @GetMapping
    @Operation(
        summary = "List branch service type summaries",
        responses = {
            @ApiResponse(responseCode = "200",
                description = "Summaries retrieved successfully",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = BranchServiceTypeSummaryDTO.class))))
        }
    )
    public ResponseEntity<List<BranchServiceTypeSummaryDTO>> listSummaries() {
        List<UUID> allowedBranchIds = extractAllowedBranchIds();
        List<BranchServiceTypeSummaryDTO> summaries = branchServiceTypeService.getBranchSummaries(allowedBranchIds);
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/{branchId}")
    @Operation(
        summary = "Get branch service type detail",
        responses = {
            @ApiResponse(responseCode = "200",
                description = "Branch detail retrieved successfully",
                content = @Content(schema = @Schema(implementation = BranchServiceTypeDetailDTO.class)))
        }
    )
    public ResponseEntity<BranchServiceTypeDetailDTO> getDetail(@PathVariable UUID branchId) {
        List<UUID> allowedBranchIds = extractAllowedBranchIds();
        BranchServiceTypeDetailDTO detail = branchServiceTypeService.getBranchDetail(branchId, allowedBranchIds);
        return ResponseEntity.ok(detail);
    }

    @PutMapping("/{branchId}")
    @Operation(
        summary = "Replace branch service type assignments",
        responses = {
            @ApiResponse(responseCode = "204", description = "Assignments updated successfully")
        }
    )
    public ResponseEntity<Void> replaceAssignments(@PathVariable UUID branchId,
                                                   @Valid @RequestBody BranchServiceTypeUpdateRequest request) {
        List<UUID> allowedBranchIds = extractAllowedBranchIds();
        branchServiceTypeService.replaceAssignments(branchId, request.getAssignments(), allowedBranchIds);
        return ResponseEntity.noContent().build();
    }

    private List<UUID> extractAllowedBranchIds() {
        try {
            Object scopeValue = resolveScopeClaim("organizationBranchIds");
            if (scopeValue == null) {
                log.debug("No organizationBranchIds in user claims");
                return Collections.emptyList();
            }

            List<UUID> branchIds = extractUUIDs(scopeValue);
            log.debug("Extracted {} allowed branch ids", branchIds.size());
            return branchIds;
        } catch (Exception ex) {
            log.error("Unable to extract organization branch scopes from user context", ex);
            return Collections.emptyList();
        }
    }

    private Object resolveScopeClaim(String claimKey) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        Object claim = extractClaim(authentication.getDetails(), claimKey);
        if (claim != null) {
            return claim;
        }

        claim = extractClaim(authentication.getPrincipal(), claimKey);
        if (claim != null) {
            return claim;
        }

        if (authentication.getAuthorities() != null) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                claim = extractClaim(authority, claimKey);
                if (claim != null) {
                    return claim;
                }
            }
        }

        return null;
    }

    private Object extractClaim(Object source, String key) {
        if (source instanceof Map<?, ?> map) {
            return map.get(key);
        }

        return null;
    }

    private List<UUID> extractUUIDs(Object scopeValue) {
        List<UUID> result = new ArrayList<>();
        if (scopeValue == null) {
            return result;
        }

        if (scopeValue instanceof UUID uuid) {
            result.add(uuid);
            return result;
        }

        if (scopeValue instanceof String str) {
            addUuidIfPresent(result, str);
            return result;
        }

        if (scopeValue instanceof Iterable<?> iterable) {
            for (Object item : iterable) {
                addUuidIfPresent(result, item);
            }
            return result;
        }

        addUuidIfPresent(result, scopeValue);
        return result;
    }

    private void addUuidIfPresent(List<UUID> target, Object value) {
        if (value == null) return;
        try {
            if (value instanceof UUID uuid) {
                target.add(uuid);
            } else if (value instanceof String str && !str.isBlank()) {
                target.add(UUID.fromString(str.trim()));
            }
        } catch (Exception ignored) {
            // ignore invalid entries
        }
    }
}


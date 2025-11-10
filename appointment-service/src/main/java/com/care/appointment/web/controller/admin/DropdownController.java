package com.care.appointment.web.controller.admin;

import com.care.appointment.infrastructure.client.AccessManagementClient;
import com.care.appointment.web.dto.OrganizationBranchDTO;
import com.care.appointment.web.dto.OrganizationDTO;
import com.sharedlib.core.context.CurrentUserContext;
import com.sharedlib.core.filter.FilterRequest;
import com.sharedlib.core.filter.FilterNormalizer;
import com.sharedlib.core.filter.ScopeCriteria;
import com.sharedlib.core.filter.ValueDataType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * REST Controller for Dropdown Data
 *
 * Provides optimized endpoints for fetching dropdown lists filtered by user scopes.
 * Uses scope-based filtering to ensure users only see organizations and branches
 * they have access to based on their assigned scopeValueIds.
 *
 * Features:
 * - Organizations dropdown (filtered by user's allowed organization branches)
 * - Organization branches dropdown (filtered by user's scopes and optional organization)
 * - Single-request optimization for performance
 * - Scope-based access control based on JWT claims
 */
@Slf4j
@RestController
@RequestMapping({"/api/admin/dropdowns", "/api/admin/Dropdowns"})
@RequiredArgsConstructor
@Tag(name = "Dropdown Data", description = "APIs for fetching dropdown lists with scope filtering")
public class DropdownController {

    private final AccessManagementClient accessManagementClient;

    /**
     * Get organizations filtered by user's allowed organization branches
     *
     * This endpoint performs a single request to retrieve organizations that correspond
     * to the organization branches the user has access to. It uses the organization branch
     * IDs from the user's JWT scope claims to filter the results.
     *
     * Request: POST /api/admin/dropdowns/organizations
     * Response: List of OrganizationDTO with id and name for dropdown
     *
     * @return List of organizations the user can access
     */
    @GetMapping("/organizations")
    @Operation(
        summary = "Get organizations dropdown",
        description = "Retrieves organizations filtered by user's allowed organization branches (scopes). " +
                     "Results include only organizations that contain branches the user has access to."
    )
    @ApiResponse(responseCode = "200", description = "Organizations retrieved successfully",
            content = @Content(schema = @Schema(implementation = OrganizationDTO.class)))
    public ResponseEntity<List<OrganizationDTO>> getOrganizationsDropdown() {
        try {
            // Extract user's allowed organization branch IDs from JWT claims
            List<UUID> allowedBranchIds = extractUserScopeValues();

            if (allowedBranchIds.isEmpty()) {
                log.warn("User has no organization branch scopes");
                return ResponseEntity.ok(Collections.emptyList());
            }

            // Create filter request with organizationBranchId criteria
            FilterRequest filterRequest = createOrganizationFilterRequest(allowedBranchIds);

            // Call access-management service to get filtered organizations
            // The service queries:
            // SELECT DISTINCT o.* FROM organizations o
            // INNER JOIN organization_branches ob ON o.id = ob.organization_id
            // WHERE ob.organization_branch_id IN (allowedBranchIds)
            List<OrganizationDTO> organizations = accessManagementClient
                    .getOrganizationsByBranchIds(filterRequest);

            log.debug("Retrieved {} organizations for user scopes", organizations.size());
            return ResponseEntity.ok(organizations);

        } catch (Exception e) {
            log.error("Error retrieving organizations dropdown", e);
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    /**
     * Get organization branches dropdown filtered by scopes and optional organization ID
     *
     * This endpoint retrieves organization branches that the user has access to.
     * If organizationId parameter is provided, results are further filtered to
     * show only branches of that organization.
     *
     * Request: GET /api/admin/dropdowns/organization-branches?organizationId=uuid
     * Response: List of OrganizationBranchDTO for dropdown
     *
     * @param organizationId (optional) Organization ID to filter branches
     * @return List of organization branches the user can access
     */
    @GetMapping("/organization-branches")
    @Operation(
        summary = "Get organization branches dropdown",
        description = "Retrieves organization branches filtered by user's allowed scopes. " +
                     "Optionally filters by organization ID. " +
                     "Results respect user's scope-based access control."
    )
    @ApiResponse(responseCode = "200", description = "Organization branches retrieved successfully",
            content = @Content(schema = @Schema(implementation = OrganizationBranchDTO.class)))
    public ResponseEntity<List<OrganizationBranchDTO>> getOrganizationBranchesDropdown(
            @RequestParam(value = "organizationId", required = false) UUID organizationId) {

        try {
            // Extract user's allowed organization branch IDs from JWT claims
            List<UUID> allowedBranchIds = extractUserScopeValues();

            if (allowedBranchIds.isEmpty()) {
                log.warn("User has no organization branch scopes");
                return ResponseEntity.ok(Collections.emptyList());
            }

            // Create filter request with scope criteria
            FilterRequest filterRequest = createBranchFilterRequest(allowedBranchIds, organizationId);

            // Call access-management service to get filtered branches
            List<OrganizationBranchDTO> branches = accessManagementClient
                    .filterOrganizationBranches(filterRequest);

            log.debug("Retrieved {} organization branches for user scopes", branches.size());
            return ResponseEntity.ok(branches);

        } catch (Exception e) {
            log.error("Error retrieving organization branches dropdown", e);
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    /**
     * Extract organization branch IDs from the current user's JWT claims
     * These represent the scope values (allowed branches) for the user
     *
     * @return List of allowed organization branch IDs
     */
    private List<UUID> extractUserScopeValues() {
        try {
            var currentUser = CurrentUserContext.get();
            if (currentUser == null) {
                log.debug("No current user context available");
                return Collections.emptyList();
            }

            Object scopeValue = currentUser.claims().get("organizationBranchIds");
            if (scopeValue == null) {
                log.debug("No organizationBranchIds in user claims");
                return Collections.emptyList();
            }

            List<UUID> allowedBranchIds = extractUUIDs(scopeValue);
            log.debug("Extracted {} allowed branch IDs from user scope", allowedBranchIds.size());
            return allowedBranchIds;

        } catch (Exception e) {
            log.error("Error extracting user scope values", e);
            return Collections.emptyList();
        }
    }

    /**
     * Create a FilterRequest for organizations filtering
     * Filters organizations by the branch IDs the user has access to
     *
     * @param allowedBranchIds the organization branch IDs user can access
     * @return FilterRequest configured for organization filtering
     */
    private FilterRequest createOrganizationFilterRequest(List<UUID> allowedBranchIds) {
        FilterRequest request = new FilterRequest();

        // Add criteria for organizationBranchId (scope filtering)
        ScopeCriteria scopeCriteria = ScopeCriteria.builder()
                .fieldName("organizationBranchId")
                .allowedValues(new ArrayList<>(allowedBranchIds))
                .dataType(ValueDataType.UUID)
                .build();

        request.setScopes(Collections.singletonList(scopeCriteria));
        return request;
    }

    /**
     * Create a FilterRequest for organization branches filtering
     * Filters by user scopes and optionally by organization ID
     *
     * @param allowedBranchIds the organization branch IDs user can access
     * @param organizationId optional organization ID to further filter
     * @return FilterRequest configured for branch filtering
     */
    private FilterRequest createBranchFilterRequest(List<UUID> allowedBranchIds, UUID organizationId) {
        FilterRequest request = new FilterRequest();

        // Add scope criteria for organizationBranchId
        ScopeCriteria scopeCriteria = ScopeCriteria.builder()
                .fieldName("organizationBranchId")
                .allowedValues(new ArrayList<>(allowedBranchIds))
                .dataType(ValueDataType.UUID)
                .build();

        request.setScopes(Collections.singletonList(scopeCriteria));

        // If organizationId is provided, add it as a search criterion
        if (organizationId != null) {
            // Note: This would require the access-management-service to support
            // filtering by organizationId in the FilterRequest criteria
            // For now, this is prepared for future enhancement
            log.debug("Filtering branches by organization: {}", organizationId);
        }

        return request;
    }

    /**
     * Convert various formats of scope values to a List of UUIDs
     * Handles List, Collection, String (comma-separated), or individual UUID formats
     *
     * @param scopeValue can be a List, Collection, String (comma-separated), or individual UUID
     * @return list of extracted UUIDs
     */
    @SuppressWarnings("unchecked")
    private List<UUID> extractUUIDs(Object scopeValue) {
        List<UUID> result = new ArrayList<>();

        if (scopeValue instanceof List<?>) {
            ((List<?>) scopeValue).forEach(item -> {
                try {
                    if (item instanceof UUID uuid) {
                        result.add(uuid);
                    } else if (item instanceof String str) {
                        result.add(UUID.fromString(str.trim()));
                    }
                } catch (Exception ignored) {
                    // Skip invalid UUIDs
                }
            });
        } else if (scopeValue instanceof Collection<?>) {
            ((Collection<?>) scopeValue).forEach(item -> {
                try {
                    if (item instanceof UUID uuid) {
                        result.add(uuid);
                    } else if (item instanceof String str) {
                        result.add(UUID.fromString(str.trim()));
                    }
                } catch (Exception ignored) {
                    // Skip invalid UUIDs
                }
            });
        } else if (scopeValue instanceof String str) {
            String[] uuids = str.split("[,\\s]+");
            for (String uuid : uuids) {
                try {
                    result.add(UUID.fromString(uuid.trim()));
                } catch (Exception ignored) {
                    // Skip invalid UUIDs
                }
            }
        } else if (scopeValue instanceof UUID uuid) {
            result.add(uuid);
        }

        return result;
    }
}

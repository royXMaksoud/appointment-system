package com.care.appointment.infrastructure.client;

import com.care.appointment.web.dto.OrganizationBranchDTO;
import com.care.appointment.web.dto.OrganizationDTO;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Feign client for accessing organization branches from access-management-service
 */
@FeignClient(
    name = "access-management-service",
    url = "${services.access-management.base-url}",
    configuration = FeignClientConfiguration.class
)
public interface AccessManagementClient {
    
    @GetMapping("/api/organization-branches/{id}")
    OrganizationBranchDTO getOrganizationBranch(@PathVariable("id") UUID id);
    
    @GetMapping("/api/organization-branches")
    List<OrganizationBranchDTO> getAllOrganizationBranches();
    
    @GetMapping("/api/organization-branches/search/nearby")
    List<OrganizationBranchDTO> searchNearbyBranches(
        @RequestParam("latitude") Double latitude,
        @RequestParam("longitude") Double longitude,
        @RequestParam(value = "radiusKm", required = false, defaultValue = "50") Integer radiusKm
    );
    
    @GetMapping("/api/organization-branches/search/by-ids")
    List<OrganizationBranchDTO> getBranchesByIds(@RequestParam("ids") List<UUID> ids);

    /**
     * Get organizations filtered by organization branch IDs
     * This uses the single-request optimization for dropdown filtering
     */
    @PostMapping("/api/dropdowns/organizations")
    List<OrganizationDTO> getOrganizationsByBranchIds(@RequestBody FilterRequest filterRequest);

    /**
     * Get organization branches with filtering support
     * Supports filtering by organization ID, scope values, and other criteria
     */
    @PostMapping("/api/organization-branches/filter")
    List<OrganizationBranchDTO> filterOrganizationBranches(@RequestBody FilterRequest filterRequest);
}


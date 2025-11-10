package com.care.appointment.web.controller;

import com.care.appointment.domain.ports.in.servicetype.LoadAllUseCase;
import com.care.appointment.domain.ports.in.servicetype.LoadUseCase;
import com.care.appointment.web.dto.admin.servicetype.ServiceTypeResponse;
import com.care.appointment.web.mapper.ServiceTypeWebMapper;
import com.sharedlib.core.filter.FilterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mobile Service Type Controller
 *
 * Provides mobile-specific endpoints for service type lookup.
 * Returns simplified DTOs optimized for mobile app consumption.
 */
@RestController
@RequestMapping("/api/mobile/service-types")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mobile - Service Types", description = "Service type endpoints for mobile app")
public class MobileServiceTypeController {

    private final LoadAllUseCase loadAllServiceTypesUseCase;
    private final LoadUseCase loadServiceTypeUseCase;
    private final ServiceTypeWebMapper mapper;

    /**
     * Get all service types for mobile dropdown/selection
     * Returns only active service types with essential fields
     * Optimized for performance on mobile networks
     */
    @GetMapping("/lookup")
    @Operation(
        summary = "Get available service types for mobile",
        description = "Returns a simplified list of all active service types for the mobile app"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Service types retrieved successfully",
        content = @Content(schema = @Schema(implementation = ServiceTypeResponse.class))
    )
    public ResponseEntity<List<ServiceTypeResponse>> getServiceTypesLookup(
            @RequestParam(required = false, defaultValue = "en") String language) {

        log.info("Fetching service types for mobile app with language: {}", language);

        try {
            // Load all service types using existing use case
            var filter = new FilterRequest();
            var pageable = Pageable.unpaged();
            var serviceTypes = loadAllServiceTypesUseCase.loadAll(filter, pageable);

            // Map to response DTOs
            List<ServiceTypeResponse> responses = serviceTypes.getContent()
                .stream()
                .filter(st -> Boolean.TRUE.equals(st.getIsActive()))
                .map(mapper::toResponse)
                .collect(Collectors.toList());

            log.info("Returning {} active service types", responses.size());
            return ResponseEntity.ok(responses);

        } catch (Exception e) {
            log.error("Error fetching service types", e);
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Get specific service type by ID
     * Returns full service type details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get service type by ID", description = "Retrieves detailed information about a specific service type")
    @ApiResponse(responseCode = "200", description = "Service type found")
    @ApiResponse(responseCode = "404", description = "Service type not found")
    public ResponseEntity<ServiceTypeResponse> getServiceTypeById(@PathVariable UUID id) {

        log.info("Fetching service type with ID: {}", id);

        var serviceType = loadServiceTypeUseCase.getServiceTypeById(id);

        if (serviceType.isEmpty()) {
            log.warn("Service type not found: {}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mapper.toResponse(serviceType.get()));
    }
}


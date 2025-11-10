package com.care.appointment.web.controller.admin;

import com.care.appointment.application.servicetype.command.CreateServiceTypeCommand;
import com.care.appointment.application.servicetype.command.UpdateServiceTypeCommand;
import com.care.appointment.domain.model.ServiceType;
import com.care.appointment.domain.ports.in.servicetype.*;
import com.care.appointment.infrastructure.db.config.ServiceTypeFilterConfig;
import com.care.appointment.infrastructure.db.entities.ServiceTypeEntity;
import com.care.appointment.web.dto.admin.servicetype.CreateServiceTypeRequest;
import com.care.appointment.web.dto.admin.servicetype.ServiceTypeResponse;
import com.care.appointment.web.dto.admin.servicetype.UpdateServiceTypeRequest;
import com.care.appointment.web.mapper.ServiceTypeWebMapper;
import com.sharedlib.core.filter.FilterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for Service Type Administration
 * 
 * Provides CRUD operations and advanced querying capabilities for managing service types.
 * Service types represent the categories and sub-categories of services offered by centers.
 * 
 * Features:
 * - Create, Update, Read, Delete operations
 * - Advanced filtering and pagination
 * - Lookup endpoints for dropdowns
 * - Metadata for dynamic filter building
 * 
 * All operations are transactional and include soft-delete support.
 */
@RestController
@RequestMapping({"/api/admin/service-types", "/api/admin/ServiceTypes"})
@RequiredArgsConstructor
@Tag(name = "Service Type Management", description = "APIs for managing service types")
public class ServiceTypeController {
    
    private final SaveUseCase saveServiceTypeUseCase;
    private final UpdateUseCase updateServiceTypeUseCase;
    private final LoadUseCase loadServiceTypeUseCase;
    private final DeleteUseCase deleteServiceTypeUseCase;
    private final LoadAllUseCase loadAllServiceTypesUseCase;
    private final ServiceTypeWebMapper mapper;
    
    /**
     * Create a new service type
     * Validates uniqueness of name before creation
     */
    @PostMapping
    @Operation(summary = "Create a new service type", description = "Creates a new service type with the provided data")
    @ApiResponse(responseCode = "201", description = "Service type created successfully",
            content = @Content(schema = @Schema(implementation = ServiceTypeResponse.class)))
    public ResponseEntity<ServiceTypeResponse> createServiceType(@Valid @RequestBody CreateServiceTypeRequest request) {
        CreateServiceTypeCommand command = mapper.toCreateCommand(request);
        ServiceType created = saveServiceTypeUseCase.saveServiceType(command);
        ServiceTypeResponse body = mapper.toResponse(created);
        return ResponseEntity
                .created(URI.create("/api/admin/service-types/" + body.getServiceTypeId()))
                .body(body);
    }
    
    /**
     * Update an existing service type
     * All fields except ID can be updated
     */
    @PutMapping("/{serviceTypeId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Update an existing service type", description = "Updates an existing service type with the provided data")
    @ApiResponse(responseCode = "200", description = "Service type updated successfully",
            content = @Content(schema = @Schema(implementation = ServiceTypeResponse.class)))
    public ResponseEntity<ServiceTypeResponse> updateServiceType(
            @PathVariable UUID serviceTypeId,
            @Valid @RequestBody UpdateServiceTypeRequest request) {
        
        UpdateServiceTypeCommand command = mapper.toUpdateCommand(serviceTypeId, request);
        ServiceType updated = updateServiceTypeUseCase.updateServiceType(command);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }
    
    /**
     * Get service type by ID
     * Returns 404 if not found
     */
    @GetMapping("/{serviceTypeId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Get service type by ID", description = "Retrieves a service type by its unique identifier")
    @ApiResponse(responseCode = "200", description = "Service type found",
            content = @Content(schema = @Schema(implementation = ServiceTypeResponse.class)))
    @ApiResponse(responseCode = "404", description = "Service type not found")
    public ResponseEntity<ServiceTypeResponse> getServiceTypeById(@PathVariable UUID serviceTypeId) {
        Optional<ServiceType> serviceType = loadServiceTypeUseCase.getServiceTypeById(serviceTypeId);
        return serviceType.map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    /**
     * Get all service types with pagination
     * Default page size is 20
     */
    @GetMapping
    @Operation(summary = "Get all service types", description = "Retrieves all service types with pagination support")
    @ApiResponse(responseCode = "200", description = "Service types retrieved successfully")
    public ResponseEntity<Page<ServiceTypeResponse>> getAllServiceTypes(@PageableDefault(size = 20) Pageable pageable) {
        FilterRequest safe = new FilterRequest();
        Page<ServiceType> serviceTypes = loadAllServiceTypesUseCase.loadAll(safe, pageable);
        Page<ServiceTypeResponse> responsePage = serviceTypes.map(mapper::toResponse);
        return ResponseEntity.ok(responsePage);
    }
    
    /**
     * Soft delete a service type
     * Sets isDeleted=true and isActive=false
     */
    @DeleteMapping("/{serviceTypeId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Delete service type by ID", description = "Deletes a service type by its unique identifier")
    @ApiResponse(responseCode = "204", description = "Service type deleted successfully")
    @ApiResponse(responseCode = "404", description = "Service type not found")
    public ResponseEntity<Void> deleteServiceType(@PathVariable UUID serviceTypeId) {
        deleteServiceTypeUseCase.deleteServiceType(serviceTypeId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Advanced filtering with custom criteria
     * Supports complex queries with multiple conditions, grouping, and sorting
     */
    @PostMapping(
            value = "/filter",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Filter service types", description = "Filters service types with advanced criteria and pagination")
    @ApiResponse(responseCode = "200", description = "Service types filtered successfully")
    public ResponseEntity<Page<ServiceTypeResponse>> filterServiceTypes(
            @RequestBody(required = false) FilterRequest request,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        FilterRequest safe = (request != null) ? request : new FilterRequest();
        Page<ServiceTypeResponse> page = loadAllServiceTypesUseCase
                .loadAll(safe, pageable)
                .map(mapper::toResponse);
        return ResponseEntity.ok(page);
    }
    
    
    /**
     * Get simplified list for dropdowns
     * Returns only active service types with id, name, and code
     */
    @GetMapping("/lookup")
    @Operation(summary = "Get all service types for dropdown", 
               description = "Returns a simple list of all active service types with id and name for use in dropdowns")
    @ApiResponse(responseCode = "200", description = "Service types lookup list retrieved successfully")
    public ResponseEntity<java.util.List<Map<String, Object>>> getServiceTypesLookup() {
        FilterRequest filter = new FilterRequest();
        Pageable pageable = Pageable.unpaged();
        Page<ServiceType> serviceTypes = loadAllServiceTypesUseCase.loadAll(filter, pageable);
        
        java.util.List<Map<String, Object>> lookup = serviceTypes.getContent().stream()
                .filter(st -> Boolean.TRUE.equals(st.getIsActive()))
                .map(st -> Map.<String, Object>of(
                        "serviceTypeId", st.getServiceTypeId(),
                        "name", st.getName(),
                        "code", st.getCode() != null ? st.getCode() : ""
                ))
                .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(lookup);
    }
}


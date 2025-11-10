package com.care.appointment.web.controller.admin;

import com.care.appointment.application.beneficiary.command.CreateBeneficiaryCommand;
import com.care.appointment.application.beneficiary.command.UpdateBeneficiaryCommand;
import com.care.appointment.application.beneficiary.command.BulkBeneficiaryUpdateCommand;
import com.care.appointment.application.beneficiary.service.BeneficiaryAdminService;
import com.care.appointment.domain.model.Beneficiary;
import com.care.appointment.domain.ports.in.beneficiary.*;
import com.care.appointment.infrastructure.db.config.BeneficiaryFilterConfig;
import com.care.appointment.infrastructure.db.entities.BeneficiaryEntity;
import com.care.appointment.web.dto.admin.beneficiary.BeneficiaryResponse;
import com.care.appointment.web.dto.admin.beneficiary.CreateBeneficiaryRequest;
import com.care.appointment.web.dto.admin.beneficiary.UpdateBeneficiaryRequest;
import com.care.appointment.web.mapper.BeneficiaryWebMapper;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for Beneficiary Administration
 *
 * Manages beneficiaries (patients/service recipients) in the system.
 * Each beneficiary represents a person who can book appointments.
 *
 * Features:
 * - CRUD operations with validation
 * - Prevents duplicate national ID and mobile numbers
 * - Advanced filtering and pagination
 * - Lookup endpoints for UI dropdowns
 * - Search by national ID, mobile, email
 * - Geo-location support (latitude/longitude)
 *
 * All operations support soft-delete and audit trail.
 */
@RestController
@RequestMapping({"/api/admin/beneficiaries", "/api/admin/Beneficiaries"})
@RequiredArgsConstructor
@Tag(name = "Beneficiary Management", description = "APIs for managing beneficiaries (patients)")
public class BeneficiaryController {

    private final SaveUseCase saveBeneficiaryUseCase;
    private final UpdateUseCase updateBeneficiaryUseCase;
    private final LoadUseCase loadBeneficiaryUseCase;
    private final DeleteUseCase deleteBeneficiaryUseCase;
    private final LoadAllUseCase loadAllBeneficiariesUseCase;
    private final BeneficiaryWebMapper mapper;
    private final BeneficiaryAdminService beneficiaryAdminService;

    /**
     * Create a new beneficiary
     * Validates uniqueness of national ID and mobile number
     */
    @PostMapping
    @Operation(summary = "Create a new beneficiary", description = "Creates a new beneficiary with validation")
    @ApiResponse(responseCode = "201", description = "Beneficiary created successfully",
            content = @Content(schema = @Schema(implementation = BeneficiaryResponse.class)))
    public ResponseEntity<BeneficiaryResponse> createBeneficiary(@Valid @RequestBody CreateBeneficiaryRequest request) {
        CreateBeneficiaryCommand command = mapper.toCreateCommand(request);
        Beneficiary created = saveBeneficiaryUseCase.saveBeneficiary(command);
        BeneficiaryResponse body = mapper.toResponse(created);
        return ResponseEntity
                .created(URI.create("/api/admin/beneficiaries/" + body.getBeneficiaryId()))
                .body(body);
    }

    /**
     * Update an existing beneficiary
     * All fields can be updated except ID
     */
    @PutMapping("/{beneficiaryId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Update an existing beneficiary", description = "Updates an existing beneficiary with new data")
    @ApiResponse(responseCode = "200", description = "Beneficiary updated successfully",
            content = @Content(schema = @Schema(implementation = BeneficiaryResponse.class)))
    public ResponseEntity<BeneficiaryResponse> updateBeneficiary(
            @PathVariable UUID beneficiaryId,
            @Valid @RequestBody UpdateBeneficiaryRequest request) {

        UpdateBeneficiaryCommand command = mapper.toUpdateCommand(beneficiaryId, request);
        Beneficiary updated = updateBeneficiaryUseCase.updateBeneficiary(command);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    /**
     * Get beneficiary by ID
     * Returns 404 if not found or deleted
     */
    @GetMapping("/{beneficiaryId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Get beneficiary by ID", description = "Retrieves a beneficiary by unique identifier")
    @ApiResponse(responseCode = "200", description = "Beneficiary found",
            content = @Content(schema = @Schema(implementation = BeneficiaryResponse.class)))
    @ApiResponse(responseCode = "404", description = "Beneficiary not found")
    public ResponseEntity<BeneficiaryResponse> getBeneficiaryById(@PathVariable UUID beneficiaryId) {
        Optional<Beneficiary> beneficiary = loadBeneficiaryUseCase.getBeneficiaryById(beneficiaryId);
        return beneficiary.map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get all beneficiaries with pagination
     * Default page size is 20, sorted by full name
     */
    @GetMapping
    @Operation(summary = "Get all beneficiaries", description = "Retrieves all beneficiaries with pagination support")
    @ApiResponse(responseCode = "200", description = "Beneficiaries retrieved successfully")
    public ResponseEntity<Page<BeneficiaryResponse>> getAllBeneficiaries(@PageableDefault(size = 20) Pageable pageable) {
        FilterRequest safe = new FilterRequest();
        Page<Beneficiary> beneficiaries = loadAllBeneficiariesUseCase.loadAll(safe, pageable);
        Page<BeneficiaryResponse> responsePage = beneficiaries.map(mapper::toResponse);
        return ResponseEntity.ok(responsePage);
    }

    /**
     * Soft delete a beneficiary
     * Marks as deleted without removing from database
     */
    @DeleteMapping("/{beneficiaryId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Delete beneficiary by ID", description = "Soft deletes a beneficiary")
    @ApiResponse(responseCode = "204", description = "Beneficiary deleted successfully")
    @ApiResponse(responseCode = "404", description = "Beneficiary not found")
    public ResponseEntity<Void> deleteBeneficiary(@PathVariable UUID beneficiaryId) {
        deleteBeneficiaryUseCase.deleteBeneficiary(beneficiaryId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Advanced filtering with dynamic criteria
     * Supports complex queries, sorting, and pagination
     */
    @PostMapping(
            value = "/filter",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Filter beneficiaries", description = "Filters beneficiaries with advanced criteria and pagination")
    @ApiResponse(responseCode = "200", description = "Beneficiaries filtered successfully")
    public ResponseEntity<Page<BeneficiaryResponse>> filterBeneficiaries(
            @RequestBody(required = false) FilterRequest request,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        FilterRequest safe = (request != null) ? request : new FilterRequest();
        Page<BeneficiaryResponse> page = loadAllBeneficiariesUseCase
                .loadAll(safe, pageable)
                .map(mapper::toResponse);
        return ResponseEntity.ok(page);
    }


    /**
     * Get simplified list for dropdowns
     * Returns active beneficiaries with id, name, and mobile
     */
    @GetMapping("/lookup")
    @Operation(summary = "Get all beneficiaries for dropdown",
            description = "Returns a simple list of all active beneficiaries for use in dropdowns")
    @ApiResponse(responseCode = "200", description = "Beneficiaries lookup list retrieved successfully")
    public ResponseEntity<java.util.List<Map<String, Object>>> getBeneficiariesLookup() {
        FilterRequest filter = new FilterRequest();
        Pageable pageable = Pageable.unpaged();
        Page<Beneficiary> beneficiaries = loadAllBeneficiariesUseCase.loadAll(filter, pageable);

        java.util.List<Map<String, Object>> lookup = beneficiaries.getContent().stream()
                .filter(Beneficiary::getIsActive)
                .filter(b -> !b.getIsDeleted())
                .map(b -> Map.<String, Object>of(
                        "beneficiaryId", b.getBeneficiaryId(),
                        "fullName", b.getFullName(),
                        "nationalId", b.getNationalId() != null ? b.getNationalId() : "",
                        "mobileNumber", b.getMobileNumber()
                ))
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(lookup);
    }
    
    /**
     * Bulk update beneficiaries
     * Updates multiple beneficiaries at once with same fields
     */
    @PutMapping("/bulk")
    @Operation(
        summary = "Bulk update beneficiaries",
        description = "Updates multiple beneficiaries with same field values"
    )
    @ApiResponse(responseCode = "200", description = "Beneficiaries updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    public ResponseEntity<List<BeneficiaryResponse>> bulkUpdateBeneficiaries(
            @Valid @RequestBody BulkBeneficiaryUpdateRequest request,
            @RequestHeader(value = "User-Id", required = false) UUID userId) {
        
        BulkBeneficiaryUpdateCommand command = BulkBeneficiaryUpdateCommand.builder()
                .beneficiaryIds(request.getBeneficiaryIds())
                .updateFields(request.getUpdateFields())
                .description(request.getDescription())
                .updatedById(userId)
                .build();
        
        List<Beneficiary> updated = beneficiaryAdminService.bulkUpdateBeneficiaries(command);
        List<BeneficiaryResponse> response = updated.stream()
                .map(mapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get beneficiary statistics
     */
    @GetMapping("/statistics")
    @Operation(
        summary = "Get beneficiary statistics",
        description = "Returns statistics about beneficiaries"
    )
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<BeneficiaryStatisticsResponse> getStatistics() {
        BeneficiaryAdminService.BeneficiaryStatistics stats = beneficiaryAdminService.getBeneficiaryStatistics();
        
        BeneficiaryStatisticsResponse response = BeneficiaryStatisticsResponse.builder()
                .totalBeneficiaries(stats.getTotalBeneficiaries())
                .activeBeneficiaries(stats.getActiveBeneficiaries())
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * DTO for bulk update request
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class BulkBeneficiaryUpdateRequest {
        @jakarta.validation.constraints.NotEmpty
        private java.util.List<UUID> beneficiaryIds;
        
        @jakarta.validation.constraints.NotEmpty
        private java.util.Map<String, Object> updateFields;
        
        @jakarta.validation.constraints.NotBlank
        private String description;
        
        @jakarta.validation.constraints.NotNull
        private Boolean isApprovedByAdmin;
    }
    
    /**
     * DTO for statistics response
     */
    @lombok.Data
    @lombok.Builder
    public static class BeneficiaryStatisticsResponse {
        private long totalBeneficiaries;
        private long activeBeneficiaries;
    }
}


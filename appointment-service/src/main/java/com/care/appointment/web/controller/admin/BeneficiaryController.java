package com.care.appointment.web.controller.admin;

import com.care.appointment.application.beneficiary.command.CreateBeneficiaryCommand;
import com.care.appointment.application.beneficiary.command.UpdateBeneficiaryCommand;
import com.care.appointment.application.beneficiary.command.BulkBeneficiaryUpdateCommand;
import com.care.appointment.application.beneficiary.service.BeneficiaryAdminService;
import com.care.appointment.infrastructure.storage.BeneficiaryDocumentStorageService;
import com.care.appointment.infrastructure.storage.BeneficiaryDocumentStorageService.StoredFile;
import com.care.appointment.domain.model.Appointment;
import com.care.appointment.domain.model.Beneficiary;
import com.care.appointment.domain.ports.in.beneficiary.*;
import com.care.appointment.domain.ports.in.appointment.ViewAppointmentUseCase;
import com.care.appointment.web.dto.admin.appointment.AppointmentDetailsResponse;
import com.care.appointment.web.dto.admin.beneficiary.BeneficiaryResponse;
import com.care.appointment.web.dto.admin.beneficiary.CreateBeneficiaryRequest;
import com.care.appointment.web.dto.admin.beneficiary.UpdateBeneficiaryRequest;
import com.care.appointment.web.mapper.AppointmentAdminWebMapper;
import com.care.appointment.web.mapper.BeneficiaryWebMapper;
import com.sharedlib.core.filter.FilterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;
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
@Slf4j
@Tag(name = "Beneficiary Management", description = "APIs for managing beneficiaries (patients)")
public class BeneficiaryController {

    private final SaveUseCase saveBeneficiaryUseCase;
    private final UpdateUseCase updateBeneficiaryUseCase;
    private final LoadUseCase loadBeneficiaryUseCase;
    private final DeleteUseCase deleteBeneficiaryUseCase;
    private final LoadAllUseCase loadAllBeneficiariesUseCase;
    private final BeneficiaryWebMapper mapper;
    private final BeneficiaryAdminService beneficiaryAdminService;
    private final ViewAppointmentUseCase viewAppointmentUseCase;
    private final AppointmentAdminWebMapper appointmentAdminWebMapper;
    private final BeneficiaryDocumentStorageService documentStorageService;

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
        BeneficiaryResponse body = decorateResponse(mapper.toResponse(created));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{beneficiaryId}")
                .buildAndExpand(Objects.requireNonNull(body.getBeneficiaryId(), "beneficiaryId"))
                .toUri();
        return ResponseEntity.created(location).body(body);
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
        return ResponseEntity.ok(decorateResponse(mapper.toResponse(updated)));
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
                .map(this::decorateResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get appointments for a beneficiary with pagination
     */
    @GetMapping("/{beneficiaryId:[0-9a-fA-F\\-]{36}}/appointments")
    @Operation(summary = "Get beneficiary appointments", description = "Retrieves all appointments for a beneficiary with pagination support")
    @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully",
            content = @Content(schema = @Schema(implementation = AppointmentDetailsResponse.class)))
    public ResponseEntity<Page<AppointmentDetailsResponse>> getBeneficiaryAppointments(
            @PathVariable UUID beneficiaryId,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<Appointment> appointments = viewAppointmentUseCase.getAppointmentsByBeneficiary(beneficiaryId, pageable);
        Page<AppointmentDetailsResponse> response = appointments.map(appointmentAdminWebMapper::toDetailsResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/{beneficiaryId:[0-9a-fA-F\\-]{36}}/profile-photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload beneficiary profile photo")
    public ResponseEntity<BeneficiaryResponse> uploadProfilePhoto(
            @PathVariable UUID beneficiaryId,
            @RequestPart("file") MultipartFile file,
            @RequestHeader(value = "User-Id", required = false) UUID userId,
            @RequestHeader(value = "X-User-Id", required = false) UUID xUserId) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile photo file is required");
        }

        Beneficiary beneficiary = loadBeneficiaryUseCase.getBeneficiaryById(beneficiaryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Beneficiary not found"));

        String previousPath = beneficiary.getProfilePhotoUrl();
        StoredFile storedFile = documentStorageService.store(beneficiaryId, file);

        Beneficiary updated = beneficiaryAdminService.updateProfilePhoto(beneficiaryId, storedFile.relativePath());

        if (StringUtils.hasText(previousPath) && !previousPath.equals(storedFile.relativePath())) {
            documentStorageService.deleteIfExists(previousPath);
        }

        BeneficiaryResponse response = decorateResponse(mapper.toResponse(updated));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{beneficiaryId:[0-9a-fA-F\\-]{36}}/profile-photo")
    @Operation(summary = "Download beneficiary profile photo")
    public ResponseEntity<Resource> downloadProfilePhoto(@PathVariable UUID beneficiaryId) {
        Beneficiary beneficiary = loadBeneficiaryUseCase.getBeneficiaryById(beneficiaryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Beneficiary not found"));

        String storedPath = beneficiary.getProfilePhotoUrl();
        if (!StringUtils.hasText(storedPath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = documentStorageService.loadAsResource(storedPath);
        MediaType resolvedMediaType = resolveMediaType(resource);
        MediaType safeMediaType = resolvedMediaType != null ? resolvedMediaType : MediaType.APPLICATION_OCTET_STREAM;

        String filename = resource.getFilename();
        if (!StringUtils.hasText(filename)) {
            filename = "profile-photo";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(Objects.requireNonNull(safeMediaType))
                .body(resource);
    }

    @DeleteMapping("/{beneficiaryId:[0-9a-fA-F\\-]{36}}/profile-photo")
    @Operation(summary = "Remove beneficiary profile photo")
    public ResponseEntity<BeneficiaryResponse> deleteProfilePhoto(@PathVariable UUID beneficiaryId) {
        Beneficiary beneficiary = loadBeneficiaryUseCase.getBeneficiaryById(beneficiaryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Beneficiary not found"));

        String storedPath = beneficiary.getProfilePhotoUrl();
        if (!StringUtils.hasText(storedPath)) {
            return ResponseEntity.noContent().build();
        }

        documentStorageService.deleteIfExists(storedPath);
        Beneficiary updated = beneficiaryAdminService.updateProfilePhoto(beneficiaryId, null);
        BeneficiaryResponse response = decorateResponse(mapper.toResponse(updated));
        return ResponseEntity.ok(response);
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
        Page<BeneficiaryResponse> responsePage = beneficiaries
                .map(domain -> decorateResponse(mapper.toResponse(domain)));
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
                .map(domain -> decorateResponse(mapper.toResponse(domain)));
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

    private BeneficiaryResponse decorateResponse(BeneficiaryResponse response) {
        if (response == null) {
            return null;
        }
        response.setProfilePhotoUrl(buildProfilePhotoUrl(
                response.getBeneficiaryId(),
                response.getProfilePhotoUrl(),
                response.getRowVersion()));
        return response;
    }

    private String buildProfilePhotoUrl(UUID beneficiaryId, String storedPath, Long version) {
        if (storedPath == null) {
            return null;
        }
        String sanitizedPath = storedPath.trim();
        if (sanitizedPath.isEmpty()) {
            return null;
        }
        if (sanitizedPath.startsWith("http")) {
            return sanitizedPath;
        }
        UUID safeBeneficiaryId = Objects.requireNonNull(beneficiaryId, "beneficiaryId");
        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/appointment-service/api/admin/beneficiaries")
                .pathSegment(safeBeneficiaryId.toString(), "profile-photo");
        if (version != null) {
            builder.queryParam("v", version.longValue());
        } else {
            builder.queryParam("ts", System.currentTimeMillis());
        }
        return builder.toUriString();
    }

    private MediaType resolveMediaType(Resource resource) {
        try {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            if (contentType != null) {
                String sanitized = contentType.trim();
                if (!sanitized.isEmpty()) {
                    return MediaType.parseMediaType(sanitized);
                }
            }
        } catch (Exception ex) {
            log.debug("Could not determine media type for profile photo: {}", ex.getMessage());
        }
        return MediaType.APPLICATION_OCTET_STREAM;
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


package com.care.appointment.web.controller.admin;

import com.care.appointment.application.holiday.command.CreateHolidayCommand;
import com.care.appointment.application.holiday.command.UpdateHolidayCommand;
import com.care.appointment.domain.model.Holiday;
import com.care.appointment.domain.ports.in.holiday.*;
import com.care.appointment.infrastructure.db.config.HolidayFilterConfig;
import com.care.appointment.infrastructure.db.entities.CenterHolidayEntity;
import com.care.appointment.web.dto.admin.holiday.CreateHolidayRequest;
import com.care.appointment.web.dto.admin.holiday.HolidayResponse;
import com.care.appointment.web.dto.admin.holiday.UpdateHolidayRequest;
import com.care.appointment.web.mapper.HolidayWebMapper;
import com.sharedlib.core.filter.FilterRequest;
import com.sharedlib.core.filter.FilterNormalizer;
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
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for Holiday Administration
 *
 * Manages holidays and off-days for organization branches/centers.
 * Each holiday defines a date when appointments should not be scheduled.
 *
 * Features:
 * - CRUD operations with validation
 * - Prevents duplicate holidays for same branch + date
 * - Supports recurring yearly holidays (e.g., national holidays)
 * - Advanced filtering and pagination
 * - Lookup endpoints for UI dropdowns
 * - Validates no past dates for new holidays
 *
 * All operations support soft-delete and audit trail.
 */
@RestController
@RequestMapping({"/api/admin/holidays", "/api/admin/Holidays"})
@RequiredArgsConstructor
@Tag(name = "Holiday Management", description = "APIs for managing center holidays and off-days")
public class HolidayController {

    private final SaveUseCase saveHolidayUseCase;
    private final UpdateUseCase updateHolidayUseCase;
    private final LoadUseCase loadHolidayUseCase;
    private final DeleteUseCase deleteHolidayUseCase;
    private final LoadAllUseCase loadAllHolidaysUseCase;
    private final HolidayWebMapper mapper;

    /**
     * Create a new holiday
     * Validates uniqueness (branch + date) and no past dates
     */
    @PostMapping
    @Operation(summary = "Create a new holiday", description = "Creates a new holiday for a center")
    @ApiResponse(responseCode = "201", description = "Holiday created successfully",
            content = @Content(schema = @Schema(implementation = HolidayResponse.class)))
    public ResponseEntity<HolidayResponse> createHoliday(@Valid @RequestBody CreateHolidayRequest request) {
        CreateHolidayCommand command = mapper.toCreateCommand(request);
        Holiday created = saveHolidayUseCase.saveHoliday(command);
        HolidayResponse body = mapper.toResponse(created);
        return ResponseEntity
                .created(URI.create("/api/admin/holidays/" + body.getHolidayId()))
                .body(body);
    }

    /**
     * Update an existing holiday
     * All fields can be updated except ID
     */
    @PutMapping("/{holidayId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Update an existing holiday", description = "Updates an existing holiday with new data")
    @ApiResponse(responseCode = "200", description = "Holiday updated successfully",
            content = @Content(schema = @Schema(implementation = HolidayResponse.class)))
    public ResponseEntity<HolidayResponse> updateHoliday(
            @PathVariable UUID holidayId,
            @Valid @RequestBody UpdateHolidayRequest request) {

        UpdateHolidayCommand command = mapper.toUpdateCommand(holidayId, request);
        Holiday updated = updateHolidayUseCase.updateHoliday(command);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    /**
     * Get holiday by ID
     * Returns 404 if not found or deleted
     */
    @GetMapping("/{holidayId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Get holiday by ID", description = "Retrieves a holiday by its unique identifier")
    @ApiResponse(responseCode = "200", description = "Holiday found",
            content = @Content(schema = @Schema(implementation = HolidayResponse.class)))
    @ApiResponse(responseCode = "404", description = "Holiday not found")
    public ResponseEntity<HolidayResponse> getHolidayById(@PathVariable UUID holidayId) {
        Optional<Holiday> holiday = loadHolidayUseCase.getHolidayById(holidayId);
        return holiday.map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get all holidays with pagination
     * Default page size is 20, sorted by holiday date
     */
    @GetMapping
    @Operation(summary = "Get all holidays", description = "Retrieves all holidays with pagination support")
    @ApiResponse(responseCode = "200", description = "Holidays retrieved successfully")
    public ResponseEntity<Page<HolidayResponse>> getAllHolidays(@PageableDefault(size = 20) Pageable pageable) {
        FilterRequest safe = new FilterRequest();
        Page<Holiday> holidays = loadAllHolidaysUseCase.loadAll(safe, pageable);
        Page<HolidayResponse> responsePage = holidays.map(mapper::toResponse);
        return ResponseEntity.ok(responsePage);
    }

    /**
     * Soft delete a holiday
     * Marks as deleted without removing from database
     */
    @DeleteMapping("/{holidayId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Delete holiday by ID", description = "Soft deletes a holiday")
    @ApiResponse(responseCode = "204", description = "Holiday deleted successfully")
    @ApiResponse(responseCode = "404", description = "Holiday not found")
    public ResponseEntity<Void> deleteHoliday(@PathVariable UUID holidayId) {
        deleteHolidayUseCase.deleteHoliday(holidayId);
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
    @Operation(summary = "Filter holidays", description = "Filters holidays with advanced criteria and pagination")
    @ApiResponse(responseCode = "200", description = "Holidays filtered successfully")
    public ResponseEntity<Page<HolidayResponse>> filterHolidays(
            @RequestBody(required = false) FilterRequest request,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        FilterRequest safe = (request != null) ? request : new FilterRequest();
        // Normalize IN criteria to ensure proper handling of array values
        FilterNormalizer.normalize(safe);

        Page<HolidayResponse> page = loadAllHolidaysUseCase
                .loadAll(safe, pageable)
                .map(mapper::toResponse);
        return ResponseEntity.ok(page);
    }

    /**
     * Get simplified list for dropdowns
     * Returns active holidays with id, name, and date
     */
    @GetMapping("/lookup")
    @Operation(summary = "Get all holidays for dropdown",
            description = "Returns a simple list of all active holidays for use in dropdowns")
    @ApiResponse(responseCode = "200", description = "Holidays lookup list retrieved successfully")
    public ResponseEntity<java.util.List<Map<String, Object>>> getHolidaysLookup() {
        FilterRequest filter = new FilterRequest();
        Pageable pageable = Pageable.unpaged();
        Page<Holiday> holidays = loadAllHolidaysUseCase.loadAll(filter, pageable);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        java.util.List<Map<String, Object>> lookup = holidays.getContent().stream()
                .filter(Holiday::getIsActive)
                .filter(h -> !h.getIsDeleted())
                .map(h -> Map.<String, Object>of(
                        "holidayId", h.getHolidayId(),
                        "organizationBranchId", h.getOrganizationBranchId(),
                        "name", h.getName(),
                        "holidayDate", h.getHolidayDate().format(formatter),
                        "isRecurringYearly", h.getIsRecurringYearly()
                ))
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(lookup);
    }
}


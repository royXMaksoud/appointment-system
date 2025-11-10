package com.care.appointment.web.controller.admin;

import com.care.appointment.application.schedule.command.CreateScheduleCommand;
import com.care.appointment.application.schedule.command.UpdateScheduleCommand;
import com.care.appointment.domain.model.Schedule;
import com.care.appointment.domain.ports.in.schedule.*;
import com.care.appointment.infrastructure.db.config.ScheduleFilterConfig;
import com.care.appointment.infrastructure.db.entities.CenterWeeklyScheduleEntity;
import com.care.appointment.web.dto.admin.schedule.CreateScheduleRequest;
import com.care.appointment.web.dto.admin.schedule.CreateScheduleBatchRequest;
import com.care.appointment.web.dto.admin.schedule.ScheduleResponse;
import com.care.appointment.web.dto.admin.schedule.UpdateScheduleRequest;
import com.care.appointment.web.mapper.ScheduleWebMapper;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for Schedule Administration
 *
 * Manages weekly schedules for organization branches/centers.
 * Each schedule defines working hours, slot duration, and capacity for a specific day of week.
 *
 * Features:
 * - CRUD operations with validation
 * - Prevents duplicate schedules for same branch + day
 * - Advanced filtering and pagination
 * - Lookup endpoints for UI dropdowns
 * - Validates time ranges (start < end)
 *
 * All operations support soft-delete and audit trail.
 */
@RestController
@RequestMapping({"/api/admin/schedules", "/api/admin/Schedules"})
@RequiredArgsConstructor
@Tag(name = "Schedule Management", description = "APIs for managing center weekly schedules")
public class ScheduleController {

    private final SaveUseCase saveScheduleUseCase;
    private final UpdateUseCase updateScheduleUseCase;
    private final LoadUseCase loadScheduleUseCase;
    private final DeleteUseCase deleteScheduleUseCase;
    private final LoadAllUseCase loadAllSchedulesUseCase;
    private final ScheduleWebMapper mapper;

    /**
     * Create a new schedule
     * Validates uniqueness (branch + day) and time range
     */
    @PostMapping
    @Operation(summary = "Create a new schedule", description = "Creates a new weekly schedule for a center")
    @ApiResponse(responseCode = "201", description = "Schedule created successfully",
            content = @Content(schema = @Schema(implementation = ScheduleResponse.class)))
    public ResponseEntity<ScheduleResponse> createSchedule(@Valid @RequestBody CreateScheduleRequest request) {
        CreateScheduleCommand command = mapper.toCreateCommand(request);
        Schedule created = saveScheduleUseCase.saveSchedule(command);
        ScheduleResponse body = mapper.toResponse(created);
        return ResponseEntity
                .created(URI.create("/api/admin/schedules/" + body.getScheduleId()))
                .body(body);
    }

    /**
     * Create multiple schedules at once (batch creation)
     * Creates one schedule for each selected day of week
     * Validates no duplicates or overlapping schedules for the same branch
     */
    @PostMapping("/batch")
    @Operation(summary = "Create multiple schedules", description = "Creates multiple schedules for selected days of week")
    @ApiResponse(responseCode = "201", description = "Schedules created successfully")
    public ResponseEntity<List<ScheduleResponse>> createScheduleBatch(@Valid @RequestBody CreateScheduleBatchRequest request) {
        List<Schedule> created = saveScheduleUseCase.saveSchedulesBatch(request);
        List<ScheduleResponse> response = created.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity
                .status(org.springframework.http.HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Update an existing schedule
     * All fields can be updated except ID
     */
    @PutMapping("/{scheduleId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Update an existing schedule", description = "Updates an existing schedule with new data")
    @ApiResponse(responseCode = "200", description = "Schedule updated successfully",
            content = @Content(schema = @Schema(implementation = ScheduleResponse.class)))
    public ResponseEntity<ScheduleResponse> updateSchedule(
            @PathVariable UUID scheduleId,
            @Valid @RequestBody UpdateScheduleRequest request) {

        UpdateScheduleCommand command = mapper.toUpdateCommand(scheduleId, request);
        Schedule updated = updateScheduleUseCase.updateSchedule(command);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    /**
     * Get schedule by ID
     * Returns 404 if not found or deleted
     */
    @GetMapping("/{scheduleId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Get schedule by ID", description = "Retrieves a schedule by its unique identifier")
    @ApiResponse(responseCode = "200", description = "Schedule found",
            content = @Content(schema = @Schema(implementation = ScheduleResponse.class)))
    @ApiResponse(responseCode = "404", description = "Schedule not found")
    public ResponseEntity<ScheduleResponse> getScheduleById(@PathVariable UUID scheduleId) {
        Optional<Schedule> schedule = loadScheduleUseCase.getScheduleById(scheduleId);
        return schedule.map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get all schedules with pagination
     * Default page size is 20, sorted by day of week
     */
    @GetMapping
    @Operation(summary = "Get all schedules", description = "Retrieves all schedules with pagination support")
    @ApiResponse(responseCode = "200", description = "Schedules retrieved successfully")
    public ResponseEntity<Page<ScheduleResponse>> getAllSchedules(@PageableDefault(size = 20) Pageable pageable) {
        FilterRequest safe = new FilterRequest();
        Page<Schedule> schedules = loadAllSchedulesUseCase.loadAll(safe, pageable);
        Page<ScheduleResponse> responsePage = schedules.map(mapper::toResponse);
        return ResponseEntity.ok(responsePage);
    }

    /**
     * Soft delete a schedule
     * Marks as deleted without removing from database
     */
    @DeleteMapping("/{scheduleId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Delete schedule by ID", description = "Soft deletes a schedule")
    @ApiResponse(responseCode = "204", description = "Schedule deleted successfully")
    @ApiResponse(responseCode = "404", description = "Schedule not found")
    public ResponseEntity<Void> deleteSchedule(@PathVariable UUID scheduleId) {
        deleteScheduleUseCase.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Advanced filtering with dynamic criteria and scope-based permission filtering
     * Supports complex queries, sorting, and pagination.
     *
     * Filter request body format:
     * {
     *   "criteria": [
     *     {
     *       "fieldName": "organizationBranchId",
     *       "operator": "IN",
     *       "value": ["uuid1", "uuid2", "uuid3"],
     *       "dataType": "UUID"
     *     }
     *   ]
     * }
     *
     * All criteria are processed through GenericSpecification for unified filtering.
     *
     * NOTE: This method applies scope-based filtering using the current user's scopes
     * from the JWT token claims. The user's allowed organizationBranchIds are extracted
     * and applied as scopes to restrict the results to only the branches the user has access to.
     */
    @PostMapping(
            value = "/filter",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Filter schedules with advanced criteria",
               description = "Filters schedules with advanced criteria and pagination. " +
                            "Criteria can include field filters with operators like IN, EQUAL, GREATER_THAN, etc. " +
                            "Results are automatically filtered by the user's permission scopes.")
    @ApiResponse(responseCode = "200", description = "Schedules filtered successfully")
    public ResponseEntity<Page<ScheduleResponse>> filterSchedules(
            @RequestBody(required = false) FilterRequest request,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        FilterRequest safe = (request != null) ? request : new FilterRequest();
        // Normalize IN criteria to ensure proper handling of array values
        FilterNormalizer.normalize(safe);

        // Apply scope-based filtering based on user's scopes from JWT claims
        applyUserScopes(safe);

        Page<ScheduleResponse> page = loadAllSchedulesUseCase
                .loadAll(safe, pageable)
                .map(mapper::toResponse);
        return ResponseEntity.ok(page);
    }

    /**
     * Extract user's scopes from the current user context and apply them to the filter.
     * The scopes restrict which organizationBranchIds the user can see based on their permissions.
     *
     * This method:
     * 1. Gets the current user from the security context
     * 2. Extracts allowed organizationBranchIds from user claims
     * 3. Adds them as scopes to the FilterRequest
     * 4. Ensures the database query only returns schedules for branches the user can access
     *
     * @param filter the FilterRequest to enhance with scopes (modified in-place)
     */
    private void applyUserScopes(FilterRequest filter) {
        try {
            var currentUser = CurrentUserContext.get();
            if (currentUser == null) {
                return;
            }

            // Extract allowed organizationBranchIds from user claims
            // The claims map contains scope information about which branches the user can access
            Object scopeValue = currentUser.claims().get("organizationBranchIds");

            if (scopeValue != null) {
                List<UUID> allowedBranchIds = extractUUIDs(scopeValue);

                if (!allowedBranchIds.isEmpty()) {
                    List<ScopeCriteria> scopes = (filter.getScopes() != null)
                        ? new java.util.ArrayList<>(filter.getScopes())
                        : new java.util.ArrayList<>();

                    // Add scope for organizationBranchId if not already present
                    boolean hasBranchScope = scopes.stream()
                            .anyMatch(s -> "organizationBranchId".equals(s.getFieldName()));

                    if (!hasBranchScope) {
                        scopes.add(ScopeCriteria.builder()
                                .fieldName("organizationBranchId")
                                .allowedValues(new java.util.ArrayList<>(allowedBranchIds))
                                .dataType(ValueDataType.UUID)
                                .build());

                        filter.setScopes(scopes);
                    }
                }
            }
        } catch (Exception e) {
            // Log but don't fail - missing scopes just means no scope-based filtering
            // The user context might not be available in some test scenarios
        }
    }

    /**
     * Convert various formats of scope values to a List of UUIDs.
     *
     * @param scopeValue can be a List, Collection, String (comma-separated), or individual UUID
     * @return list of extracted UUIDs
     */
    @SuppressWarnings("unchecked")
    private List<UUID> extractUUIDs(Object scopeValue) {
        List<UUID> result = new java.util.ArrayList<>();

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

    /**
     * Get simplified list for dropdowns
     * Returns active schedules with id, branch, day, and times
     */
    @GetMapping("/lookup")
    @Operation(summary = "Get all schedules for dropdown",
            description = "Returns a simple list of all active schedules for use in dropdowns")
    @ApiResponse(responseCode = "200", description = "Schedules lookup list retrieved successfully")
    public ResponseEntity<java.util.List<Map<String, Object>>> getSchedulesLookup() {
        FilterRequest filter = new FilterRequest();
        Pageable pageable = Pageable.unpaged();
        Page<Schedule> schedules = loadAllSchedulesUseCase.loadAll(filter, pageable);

        java.util.List<Map<String, Object>> lookup = schedules.getContent().stream()
                .filter(Schedule::getIsActive)
                .filter(s -> !s.getIsDeleted())
                .map(s -> Map.<String, Object>of(
                        "scheduleId", s.getScheduleId(),
                        "organizationBranchId", s.getOrganizationBranchId(),
                        "dayOfWeek", s.getDayOfWeek(),
                        "dayName", getDayName(s.getDayOfWeek()),
                        "startTime", s.getStartTime().toString(),
                        "endTime", s.getEndTime().toString(),
                        "slotDurationMinutes", s.getSlotDurationMinutes()
                ))
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(lookup);
    }

    private String getDayName(Integer dayOfWeek) {
        if (dayOfWeek == null) return "Unknown";
        return switch (dayOfWeek) {
            case 0 -> "Sunday";
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            case 4 -> "Thursday";
            case 5 -> "Friday";
            case 6 -> "Saturday";
            default -> "Unknown";
        };
    }
}


package com.care.appointment.web.controller.admin;

import com.care.appointment.application.actiontype.command.CreateActionTypeCommand;
import com.care.appointment.application.actiontype.command.UpdateActionTypeCommand;
import com.care.appointment.domain.model.ActionType;
import com.care.appointment.domain.ports.in.actiontype.*;
import com.care.appointment.infrastructure.db.config.ActionTypeFilterConfig;
import com.care.appointment.infrastructure.db.entities.AppointmentActionTypeEntity;
import com.care.appointment.web.dto.admin.actiontype.CreateActionTypeRequest;
import com.care.appointment.web.dto.admin.actiontype.ActionTypeResponse;
import com.care.appointment.web.dto.admin.actiontype.UpdateActionTypeRequest;
import com.care.appointment.web.mapper.ActionTypeWebMapper;
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
 * REST Controller for Action Type Administration
 * 
 * Manages action/outcome types that represent possible results of appointments.
 * Action types can indicate completion status, transfer requirements, or other outcomes.
 * 
 * Features:
 * - Full CRUD operations with validation
 * - Advanced filtering and pagination
 * - Lookup endpoints for UI dropdowns
 * - Business logic flags (requiresTransfer, completesAppointment)
 * 
 * All operations support soft-delete and audit trail.
 */
@RestController
@RequestMapping({"/api/admin/action-types", "/api/admin/ActionTypes"})
@RequiredArgsConstructor
@Tag(name = "Action Type Management", description = "APIs for managing appointment action types")
public class ActionTypeController {
    
    private final SaveUseCase saveActionTypeUseCase;
    private final UpdateUseCase updateActionTypeUseCase;
    private final LoadUseCase loadActionTypeUseCase;
    private final DeleteUseCase deleteActionTypeUseCase;
    private final LoadAllUseCase loadAllActionTypesUseCase;
    private final ActionTypeWebMapper mapper;
    
    /**
     * Create a new action type
     * Validates uniqueness of code before creation
     */
    @PostMapping
    @Operation(summary = "Create a new action type", description = "Creates a new action type with the provided data")
    @ApiResponse(responseCode = "201", description = "Action type created successfully",
            content = @Content(schema = @Schema(implementation = ActionTypeResponse.class)))
    public ResponseEntity<ActionTypeResponse> createActionType(@Valid @RequestBody CreateActionTypeRequest request) {
        CreateActionTypeCommand command = mapper.toCreateCommand(request);
        ActionType created = saveActionTypeUseCase.saveActionType(command);
        ActionTypeResponse body = mapper.toResponse(created);
        return ResponseEntity
                .created(URI.create("/api/admin/action-types/" + body.getActionTypeId()))
                .body(body);
    }
    
    /**
     * Update an existing action type
     * Code field cannot be updated (it's the unique identifier)
     */
    @PutMapping("/{actionTypeId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Update an existing action type", description = "Updates an existing action type with the provided data")
    @ApiResponse(responseCode = "200", description = "Action type updated successfully",
            content = @Content(schema = @Schema(implementation = ActionTypeResponse.class)))
    public ResponseEntity<ActionTypeResponse> updateActionType(
            @PathVariable UUID actionTypeId,
            @Valid @RequestBody UpdateActionTypeRequest request) {
        
        UpdateActionTypeCommand command = mapper.toUpdateCommand(actionTypeId, request);
        ActionType updated = updateActionTypeUseCase.updateActionType(command);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }
    
    /**
     * Get action type by ID
     * Returns 404 if not found or deleted
     */
    @GetMapping("/{actionTypeId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Get action type by ID", description = "Retrieves an action type by its unique identifier")
    @ApiResponse(responseCode = "200", description = "Action type found",
            content = @Content(schema = @Schema(implementation = ActionTypeResponse.class)))
    @ApiResponse(responseCode = "404", description = "Action type not found")
    public ResponseEntity<ActionTypeResponse> getActionTypeById(@PathVariable UUID actionTypeId) {
        Optional<ActionType> actionType = loadActionTypeUseCase.getActionTypeById(actionTypeId);
        return actionType.map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    /**
     * Get all action types with pagination
     * Default page size is 20, sorted by displayOrder
     */
    @GetMapping
    @Operation(summary = "Get all action types", description = "Retrieves all action types with pagination support")
    @ApiResponse(responseCode = "200", description = "Action types retrieved successfully")
    public ResponseEntity<Page<ActionTypeResponse>> getAllActionTypes(@PageableDefault(size = 20) Pageable pageable) {
        FilterRequest safe = new FilterRequest();
        Page<ActionType> actionTypes = loadAllActionTypesUseCase.loadAll(safe, pageable);
        Page<ActionTypeResponse> responsePage = actionTypes.map(mapper::toResponse);
        return ResponseEntity.ok(responsePage);
    }
    
    /**
     * Soft delete an action type
     * Marks as deleted without removing from database
     */
    @DeleteMapping("/{actionTypeId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Delete action type by ID", description = "Deletes an action type by its unique identifier")
    @ApiResponse(responseCode = "204", description = "Action type deleted successfully")
    @ApiResponse(responseCode = "404", description = "Action type not found")
    public ResponseEntity<Void> deleteActionType(@PathVariable UUID actionTypeId) {
        deleteActionTypeUseCase.deleteActionType(actionTypeId);
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
    @Operation(summary = "Filter action types", description = "Filters action types with advanced criteria and pagination")
    @ApiResponse(responseCode = "200", description = "Action types filtered successfully")
    public ResponseEntity<Page<ActionTypeResponse>> filterActionTypes(
            @RequestBody(required = false) FilterRequest request,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        FilterRequest safe = (request != null) ? request : new FilterRequest();
        Page<ActionTypeResponse> page = loadAllActionTypesUseCase
                .loadAll(safe, pageable)
                .map(mapper::toResponse);
        return ResponseEntity.ok(page);
    }
    
    
    /**
     * Get simplified list for dropdowns
     * Returns only active types with id, name, code, and business flags
     */
    @GetMapping("/lookup")
    @Operation(summary = "Get all action types for dropdown", 
               description = "Returns a simple list of all active action types with id, name, and code for use in dropdowns")
    @ApiResponse(responseCode = "200", description = "Action types lookup list retrieved successfully")
    public ResponseEntity<java.util.List<Map<String, Object>>> getActionTypesLookup() {
        FilterRequest filter = new FilterRequest();
        Pageable pageable = Pageable.unpaged();
        Page<ActionType> actionTypes = loadAllActionTypesUseCase.loadAll(filter, pageable);
        
        java.util.List<Map<String, Object>> lookup = actionTypes.getContent().stream()
                .filter(at -> Boolean.TRUE.equals(at.getIsActive()))
                .map(at -> Map.<String, Object>of(
                        "actionTypeId", at.getActionTypeId(),
                        "name", at.getName(),
                        "code", at.getCode(),
                        "requiresTransfer", at.getRequiresTransfer(),
                        "completesAppointment", at.getCompletesAppointment()
                ))
                .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(lookup);
    }
}


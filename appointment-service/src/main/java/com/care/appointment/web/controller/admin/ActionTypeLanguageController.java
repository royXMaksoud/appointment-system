package com.care.appointment.web.controller.admin;

import com.care.appointment.application.actiontypelanguage.command.CreateActionTypeLanguageCommand;
import com.care.appointment.application.actiontypelanguage.command.UpdateActionTypeLanguageCommand;
import com.care.appointment.domain.model.ActionTypeLanguage;
import com.care.appointment.domain.ports.in.actiontypelanguage.*;
import com.care.appointment.web.dto.admin.actiontype.ActionTypeLanguageResponse;
import com.care.appointment.web.dto.admin.actiontype.CreateActionTypeLanguageRequest;
import com.care.appointment.web.dto.admin.actiontype.UpdateActionTypeLanguageRequest;
import com.care.appointment.web.mapper.ActionTypeLanguageWebMapper;
import com.sharedlib.core.filter.FilterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/admin/action-type-languages")
@RequiredArgsConstructor
@Tag(name = "Action Type Languages", description = "Manage localized names for appointment action types")
public class ActionTypeLanguageController {

    private final SaveUseCase saveUseCase;
    private final UpdateUseCase updateUseCase;
    private final LoadUseCase loadUseCase;
    private final DeleteUseCase deleteUseCase;
    private final LoadAllUseCase loadAllUseCase;
    private final ActionTypeLanguageWebMapper mapper;

    @PostMapping
    @Operation(summary = "Create action type language")
    @ApiResponse(responseCode = "201", description = "Action type language created",
            content = @Content(schema = @Schema(implementation = ActionTypeLanguageResponse.class)))
    public ResponseEntity<ActionTypeLanguageResponse> create(@Valid @RequestBody CreateActionTypeLanguageRequest request) {
        CreateActionTypeLanguageCommand command = mapper.toCreateCommand(request);
        ActionTypeLanguage created = saveUseCase.saveActionTypeLanguage(command);
        ActionTypeLanguageResponse response = mapper.toResponse(created);
        return ResponseEntity.created(URI.create("/api/admin/action-type-languages/" + response.getActionTypeLanguageId()))
                .body(response);
    }

    @PutMapping("/{id:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Update action type language")
    public ResponseEntity<ActionTypeLanguageResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateActionTypeLanguageRequest request
    ) {
        UpdateActionTypeLanguageCommand command = mapper.toUpdateCommand(id, request);
        ActionTypeLanguage updated = updateUseCase.updateActionTypeLanguage(command);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @GetMapping("/{id:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Get action type language by id")
    public ResponseEntity<ActionTypeLanguageResponse> getById(@PathVariable UUID id) {
        Optional<ActionTypeLanguage> language = loadUseCase.getActionTypeLanguageById(id);
        return language.map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Delete action type language")
    @ApiResponse(responseCode = "204", description = "Action type language deleted")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteUseCase.deleteActionTypeLanguage(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/filter")
    @Operation(summary = "Filter action type languages")
    public ResponseEntity<Page<ActionTypeLanguageResponse>> filter(
            @RequestBody(required = false) FilterRequest filterRequest,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        FilterRequest effective = filterRequest != null ? filterRequest : new FilterRequest();
        Page<ActionTypeLanguageResponse> page = loadAllUseCase
                .loadAllActionTypeLanguages(effective, pageable)
                .map(mapper::toResponse);
        return ResponseEntity.ok(page);
    }

}



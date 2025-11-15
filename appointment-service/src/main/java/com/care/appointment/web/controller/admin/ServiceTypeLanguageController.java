package com.care.appointment.web.controller.admin;

import com.care.appointment.application.servicetypelanguage.command.CreateServiceTypeLanguageCommand;
import com.care.appointment.application.servicetypelanguage.command.UpdateServiceTypeLanguageCommand;
import com.care.appointment.domain.model.ServiceTypeLanguage;
import com.care.appointment.domain.ports.in.servicetypelanguage.*;
import com.care.appointment.web.dto.admin.servicetype.CreateServiceTypeLanguageRequest;
import com.care.appointment.web.dto.admin.servicetype.ServiceTypeLanguageResponse;
import com.care.appointment.web.dto.admin.servicetype.UpdateServiceTypeLanguageRequest;
import com.care.appointment.web.mapper.ServiceTypeLanguageWebMapper;
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
@RequestMapping("/api/admin/service-type-languages")
@RequiredArgsConstructor
@Tag(name = "Service Type Languages", description = "Manage localized labels for service types")
public class ServiceTypeLanguageController {

    private final SaveUseCase saveUseCase;
    private final UpdateUseCase updateUseCase;
    private final LoadUseCase loadUseCase;
    private final DeleteUseCase deleteUseCase;
    private final LoadAllUseCase loadAllUseCase;
    private final ServiceTypeLanguageWebMapper mapper;

    @PostMapping
    @Operation(summary = "Create service type language")
    @ApiResponse(responseCode = "201", description = "Service type language created",
            content = @Content(schema = @Schema(implementation = ServiceTypeLanguageResponse.class)))
    public ResponseEntity<ServiceTypeLanguageResponse> create(@Valid @RequestBody CreateServiceTypeLanguageRequest request) {
        CreateServiceTypeLanguageCommand command = mapper.toCreateCommand(request);
        ServiceTypeLanguage created = saveUseCase.saveServiceTypeLanguage(command);
        ServiceTypeLanguageResponse response = mapper.toResponse(created);
        return ResponseEntity.created(URI.create("/api/admin/service-type-languages/" + response.getServiceTypeLanguageId()))
                .body(response);
    }

    @PutMapping("/{id:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Update service type language")
    public ResponseEntity<ServiceTypeLanguageResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateServiceTypeLanguageRequest request
    ) {
        UpdateServiceTypeLanguageCommand command = mapper.toUpdateCommand(id, request);
        ServiceTypeLanguage updated = updateUseCase.updateServiceTypeLanguage(command);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @GetMapping("/{id:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Get service type language by id")
    public ResponseEntity<ServiceTypeLanguageResponse> getById(@PathVariable UUID id) {
        Optional<ServiceTypeLanguage> language = loadUseCase.getServiceTypeLanguageById(id);
        return language.map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Delete service type language")
    @ApiResponse(responseCode = "204", description = "Service type language deleted")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteUseCase.deleteServiceTypeLanguage(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/filter")
    @Operation(summary = "Filter service type languages")
    public ResponseEntity<Page<ServiceTypeLanguageResponse>> filter(
            @RequestBody(required = false) FilterRequest filterRequest,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        FilterRequest effective = filterRequest != null ? filterRequest : new FilterRequest();
        Page<ServiceTypeLanguageResponse> page = loadAllUseCase
                .loadAllServiceTypeLanguages(effective, pageable)
                .map(mapper::toResponse);
        return ResponseEntity.ok(page);
    }
}



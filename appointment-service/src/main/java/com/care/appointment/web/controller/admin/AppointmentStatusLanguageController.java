package com.care.appointment.web.controller.admin;

import com.care.appointment.application.appointmentstatuslanguage.command.CreateAppointmentStatusLanguageCommand;
import com.care.appointment.application.appointmentstatuslanguage.command.UpdateAppointmentStatusLanguageCommand;
import com.care.appointment.domain.model.AppointmentStatusLanguage;
import com.care.appointment.domain.ports.in.appointmentstatuslanguage.*;
import com.care.appointment.web.dto.admin.appointmentstatus.AppointmentStatusLanguageResponse;
import com.care.appointment.web.dto.admin.appointmentstatus.CreateAppointmentStatusLanguageRequest;
import com.care.appointment.web.dto.admin.appointmentstatus.UpdateAppointmentStatusLanguageRequest;
import com.care.appointment.web.mapper.AppointmentStatusLanguageWebMapper;
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
@RequestMapping("/api/admin/appointment-status-languages")
@RequiredArgsConstructor
@Tag(name = "Appointment Status Languages", description = "Manage localized names for appointment statuses")
public class AppointmentStatusLanguageController {

    private final SaveUseCase saveUseCase;
    private final UpdateUseCase updateUseCase;
    private final LoadUseCase loadUseCase;
    private final DeleteUseCase deleteUseCase;
    private final LoadAllUseCase loadAllUseCase;
    private final AppointmentStatusLanguageWebMapper mapper;

    @PostMapping
    @Operation(summary = "Create appointment status language")
    @ApiResponse(responseCode = "201", description = "Appointment status language created",
            content = @Content(schema = @Schema(implementation = AppointmentStatusLanguageResponse.class)))
    public ResponseEntity<AppointmentStatusLanguageResponse> create(@Valid @RequestBody CreateAppointmentStatusLanguageRequest request) {
        CreateAppointmentStatusLanguageCommand command = mapper.toCreateCommand(request);
        AppointmentStatusLanguage created = saveUseCase.saveAppointmentStatusLanguage(command);
        AppointmentStatusLanguageResponse response = mapper.toResponse(created);
        return ResponseEntity.created(URI.create("/api/admin/appointment-status-languages/" + response.getAppointmentStatusLanguageId()))
                .body(response);
    }

    @PutMapping("/{id:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Update appointment status language")
    public ResponseEntity<AppointmentStatusLanguageResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAppointmentStatusLanguageRequest request
    ) {
        UpdateAppointmentStatusLanguageCommand command = mapper.toUpdateCommand(id, request);
        AppointmentStatusLanguage updated = updateUseCase.updateAppointmentStatusLanguage(command);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @GetMapping("/{id:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Get appointment status language by id")
    public ResponseEntity<AppointmentStatusLanguageResponse> getById(@PathVariable UUID id) {
        Optional<AppointmentStatusLanguage> language = loadUseCase.getAppointmentStatusLanguageById(id);
        return language.map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Delete appointment status language")
    @ApiResponse(responseCode = "204", description = "Appointment status language deleted")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteUseCase.deleteAppointmentStatusLanguage(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/filter")
    @Operation(summary = "Filter appointment status languages")
    public ResponseEntity<Page<AppointmentStatusLanguageResponse>> filter(
            @RequestBody(required = false) FilterRequest filterRequest,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        FilterRequest effective = filterRequest != null ? filterRequest : new FilterRequest();
        Page<AppointmentStatusLanguageResponse> page = loadAllUseCase
                .loadAllAppointmentStatusLanguages(effective, pageable)
                .map(mapper::toResponse);
        return ResponseEntity.ok(page);
    }

}



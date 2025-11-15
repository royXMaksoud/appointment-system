package com.care.appointment.web.controller.admin;

import com.care.appointment.application.appointmentstatus.command.CreateAppointmentStatusCommand;
import com.care.appointment.application.appointmentstatus.command.UpdateAppointmentStatusCommand;
import com.care.appointment.domain.model.AppointmentStatus;
import com.care.appointment.domain.ports.in.appointmentstatus.*;
import com.care.appointment.infrastructure.db.entities.AppointmentStatusLangEntity;
import com.care.appointment.infrastructure.db.repositories.AppointmentStatusLangRepository;
import com.care.appointment.infrastructure.db.repositories.AppointmentStatusRepository;
import com.care.appointment.web.dto.admin.appointmentstatus.AppointmentStatusResponse;
import com.care.appointment.web.dto.admin.appointmentstatus.CreateAppointmentStatusRequest;
import com.care.appointment.web.dto.admin.appointmentstatus.UpdateAppointmentStatusRequest;
import com.care.appointment.web.mapper.AppointmentStatusWebMapper;
import com.care.appointment.web.dto.common.LookupItemResponse;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/admin/appointment-statuses")
@RequiredArgsConstructor
@Tag(name = "Appointment Status Management", description = "APIs for managing appointment statuses")
public class AppointmentStatusController {

    private final SaveUseCase saveUseCase;
    private final UpdateUseCase updateUseCase;
    private final LoadUseCase loadUseCase;
    private final DeleteUseCase deleteUseCase;
    private final LoadAllUseCase loadAllUseCase;
    private final AppointmentStatusWebMapper mapper;
    private final AppointmentStatusRepository statusRepository;
    private final AppointmentStatusLangRepository statusLangRepository;

    @PostMapping
    @Operation(summary = "Create appointment status")
    @ApiResponse(responseCode = "201", description = "Appointment status created",
            content = @Content(schema = @Schema(implementation = AppointmentStatusResponse.class)))
    public ResponseEntity<AppointmentStatusResponse> create(@Valid @RequestBody CreateAppointmentStatusRequest request) {
        CreateAppointmentStatusCommand command = mapper.toCreateCommand(request);
        AppointmentStatus created = saveUseCase.saveAppointmentStatus(command);
        AppointmentStatusResponse response = mapper.toResponse(created);
        return ResponseEntity.created(URI.create("/api/admin/appointment-statuses/" + response.getAppointmentStatusId()))
                .body(response);
    }

    @PutMapping("/{id:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Update appointment status")
    public ResponseEntity<AppointmentStatusResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAppointmentStatusRequest request
    ) {
        UpdateAppointmentStatusCommand command = mapper.toUpdateCommand(id, request);
        AppointmentStatus updated = updateUseCase.updateAppointmentStatus(command);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @GetMapping("/{id:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Get appointment status by id")
    public ResponseEntity<AppointmentStatusResponse> getById(@PathVariable UUID id) {
        Optional<AppointmentStatus> status = loadUseCase.getAppointmentStatusById(id);
        return status.map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Delete appointment status")
    @ApiResponse(responseCode = "204", description = "Appointment status deleted")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteUseCase.deleteAppointmentStatus(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/filter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Filter appointment statuses")
    public ResponseEntity<Page<AppointmentStatusResponse>> filter(
            @RequestBody(required = false) FilterRequest filterRequest,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        FilterRequest effective = filterRequest != null ? filterRequest : new FilterRequest();
        Page<AppointmentStatusResponse> page = loadAllUseCase
                .loadAllAppointmentStatuses(effective, pageable)
                .map(mapper::toResponse);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/lookup")
    @Operation(summary = "Get appointment statuses for dropdowns")
    public ResponseEntity<List<LookupItemResponse>> lookup(
            @RequestParam(value = "lang", required = false) String languageCode
    ) {
        String lang = (languageCode == null || languageCode.isBlank())
                ? Locale.ENGLISH.getLanguage()
                : languageCode.toLowerCase(Locale.ROOT);

        Map<UUID, String> namesById = statusLangRepository.findByLanguageCodeAndIsActiveTrue(lang)
                .stream()
                .collect(Collectors.toMap(
                        AppointmentStatusLangEntity::getAppointmentStatusId,
                        AppointmentStatusLangEntity::getName,
                        (existing, ignored) -> existing
                ));

        List<LookupItemResponse> response = statusRepository.findByIsActiveTrue().stream()
                .filter(status -> !Boolean.TRUE.equals(status.getIsDeleted()))
                .map(status -> {
                    String label = namesById.get(status.getAppointmentStatusId());
                    if (label == null || label.isBlank()) {
                        label = status.getName() != null && !status.getName().isBlank()
                                ? status.getName()
                                : status.getCode();
                    }
                    return LookupItemResponse.builder()
                            .value(status.getAppointmentStatusId())
                            .code(status.getCode())
                            .label(label)
                            .name(status.getName())
                            .build();
                })
                .toList();
        return ResponseEntity.ok(response);
    }
}



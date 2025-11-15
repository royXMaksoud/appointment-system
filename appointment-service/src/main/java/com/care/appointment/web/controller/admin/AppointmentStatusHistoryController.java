package com.care.appointment.web.controller.admin;

import com.care.appointment.application.common.service.UserDirectoryService;
import com.care.appointment.application.statushistory.command.CreateAppointmentStatusHistoryCommand;
import com.care.appointment.application.statushistory.command.UpdateAppointmentStatusHistoryCommand;
import com.care.appointment.application.statushistory.service.AppointmentStatusHistoryService;
import com.care.appointment.domain.model.AppointmentStatusHistory;
import com.care.appointment.web.dto.admin.appointment.history.AppointmentStatusHistoryResponse;
import com.care.appointment.web.dto.admin.appointment.history.CreateAppointmentStatusHistoryRequest;
import com.care.appointment.web.dto.admin.appointment.history.UpdateAppointmentStatusHistoryRequest;
import com.care.appointment.web.mapper.AppointmentStatusHistoryWebMapper;
import com.sharedlib.core.filter.FilterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/appointments/status-history")
@RequiredArgsConstructor
@Tag(name = "Appointment Status History", description = "CRUD operations for appointment status history")
public class AppointmentStatusHistoryController {

    private final AppointmentStatusHistoryService historyService;
    private final AppointmentStatusHistoryWebMapper mapper;
    private final UserDirectoryService userDirectoryService;

    @PostMapping
    @Operation(summary = "Create new status history entry")
    public ResponseEntity<AppointmentStatusHistoryResponse> create(
            @Valid @RequestBody CreateAppointmentStatusHistoryRequest request) {

        CreateAppointmentStatusHistoryCommand command = mapper.toCreateCommand(request);
        AppointmentStatusHistory history = historyService.createHistory(command);
        AppointmentStatusHistoryResponse response = mapper.toResponse(history);
        enrichSingle(response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{historyId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Update status history entry")
    public ResponseEntity<AppointmentStatusHistoryResponse> update(
            @PathVariable UUID historyId,
            @Valid @RequestBody UpdateAppointmentStatusHistoryRequest request) {

        UpdateAppointmentStatusHistoryCommand command = mapper.toUpdateCommand(historyId, request);
        AppointmentStatusHistory history = historyService.updateHistory(command);
        AppointmentStatusHistoryResponse response = mapper.toResponse(history);
        enrichSingle(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{historyId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Get status history entry by ID")
    public ResponseEntity<AppointmentStatusHistoryResponse> getById(@PathVariable UUID historyId) {
        return historyService.getById(historyId)
                .map(mapper::toResponse)
                .map(response -> {
                    enrichSingle(response);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{historyId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Delete status history entry")
    public ResponseEntity<Void> delete(@PathVariable UUID historyId) {
        historyService.delete(historyId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    @Operation(summary = "Search status history entries with filters")
    public ResponseEntity<Page<AppointmentStatusHistoryResponse>> search(
            @RequestBody(required = false) FilterRequest filter,
            @PageableDefault(size = 20) Pageable pageable) {

        FilterRequest safeFilter = filter != null ? filter : new FilterRequest();
        Page<AppointmentStatusHistoryResponse> page = historyService.loadAll(safeFilter, pageable)
                .map(mapper::toResponse);
        return ResponseEntity.ok(enrichWithUserNames(page));
    }

    /**
     * Alias to support "/filter" convention used by generic CRUD component on the frontend.
     */
    @PostMapping("/filter")
    @Operation(summary = "Alias for search history entries with filters")
    public ResponseEntity<Page<AppointmentStatusHistoryResponse>> filterAlias(
            @RequestBody(required = false) FilterRequest filter,
            @PageableDefault(size = 20) Pageable pageable) {
        return search(filter, pageable);
    }

    @SuppressWarnings("null")
    private Page<AppointmentStatusHistoryResponse> enrichWithUserNames(Page<AppointmentStatusHistoryResponse> page) {
        if (page == null || page.isEmpty()) {
            return page;
        }

        List<AppointmentStatusHistoryResponse> originalContent = new ArrayList<>(page.getContent());

        var userIds = originalContent.stream()
                .map(AppointmentStatusHistoryResponse::getChangedByUserId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        var names = userDirectoryService.getDisplayNames(userIds);

        var content = originalContent.stream()
                .map(item -> {
                    var builder = item.toBuilder();
                    if (item.getChangedByUserId() != null) {
                        builder.changedByName(names.get(item.getChangedByUserId()));
                    }
                    return builder.build();
                })
                .collect(Collectors.toList());

        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    }

    private void enrichSingle(AppointmentStatusHistoryResponse response) {
        if (response == null || response.getChangedByUserId() == null) {
            return;
        }
        var names = userDirectoryService.getDisplayNames(Collections.singleton(response.getChangedByUserId()));
        response.setChangedByName(names.get(response.getChangedByUserId()));
    }
}


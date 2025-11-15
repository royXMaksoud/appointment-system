package com.care.appointment.web.controller.admin;

import com.care.appointment.application.common.service.UserDirectoryService;
import com.care.appointment.application.referral.command.CreateAppointmentReferralCommand;
import com.care.appointment.application.referral.command.UpdateAppointmentReferralCommand;
import com.care.appointment.application.referral.service.AppointmentReferralService;
import com.care.appointment.domain.model.AppointmentReferral;
import com.care.appointment.web.dto.admin.referral.AppointmentReferralResponse;
import com.care.appointment.web.dto.admin.referral.CreateAppointmentReferralRequest;
import com.care.appointment.web.dto.admin.referral.UpdateAppointmentReferralRequest;
import com.care.appointment.web.mapper.AppointmentReferralWebMapper;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/appointments/referrals")
@RequiredArgsConstructor
@Tag(name = "Appointment Referrals", description = "CRUD operations for appointment referrals")
public class AppointmentReferralController {

    private final AppointmentReferralService referralService;
    private final AppointmentReferralWebMapper mapper;
    private final UserDirectoryService userDirectoryService;

    @PostMapping
    @Operation(summary = "Create a new referral")
    public ResponseEntity<AppointmentReferralResponse> create(
            @Valid @RequestBody CreateAppointmentReferralRequest request) {

        CreateAppointmentReferralCommand command = mapper.toCreateCommand(request);
        AppointmentReferral referral = referralService.createReferral(command);
        AppointmentReferralResponse response = mapper.toResponse(referral);
        enrichSingle(response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{referralId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Update referral")
    public ResponseEntity<AppointmentReferralResponse> update(
            @PathVariable UUID referralId,
            @Valid @RequestBody UpdateAppointmentReferralRequest request) {

        UpdateAppointmentReferralCommand command = mapper.toUpdateCommand(referralId, request);
        AppointmentReferral referral = referralService.updateReferral(command);
        AppointmentReferralResponse response = mapper.toResponse(referral);
        enrichSingle(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{referralId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Get referral by ID")
    public ResponseEntity<AppointmentReferralResponse> getById(@PathVariable UUID referralId) {
        return referralService.getById(referralId)
                .map(mapper::toResponse)
                .map(response -> {
                    enrichSingle(response);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{referralId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Delete referral")
    public ResponseEntity<Void> delete(@PathVariable UUID referralId) {
        referralService.delete(referralId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    @Operation(summary = "Search referrals with filters")
    public ResponseEntity<Page<AppointmentReferralResponse>> search(
            @RequestBody(required = false) FilterRequest filter,
            @PageableDefault(size = 20) Pageable pageable) {

        FilterRequest safeFilter = filter != null ? filter : new FilterRequest();
        Page<AppointmentReferralResponse> responses = referralService.loadAll(safeFilter, pageable)
                .map(mapper::toResponse);
        return ResponseEntity.ok(enrichWithUserNames(responses));
    }

    /**
     * Alias to support "/filter" convention used by the generic CRUD component on the frontend.
     */
    @PostMapping("/filter")
    @Operation(summary = "Alias for search referrals with filters")
    public ResponseEntity<Page<AppointmentReferralResponse>> filterAlias(
            @RequestBody(required = false) FilterRequest filter,
            @PageableDefault(size = 20) Pageable pageable) {
        return search(filter, pageable);
    }

    @SuppressWarnings("null")
    private Page<AppointmentReferralResponse> enrichWithUserNames(Page<AppointmentReferralResponse> page) {
        if (page == null || page.isEmpty()) {
            return page;
        }

        List<AppointmentReferralResponse> originalContent = new ArrayList<>(page.getContent());
        var userIds = originalContent.stream()
                .flatMap(item -> java.util.stream.Stream.of(item.getCreatedById(), item.getUpdatedById()))
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        var names = userDirectoryService.getDisplayNames(userIds);

        var content = originalContent.stream()
                .map(item -> {
                    var builder = item.toBuilder();
                    if (item.getCreatedById() != null) {
                        builder.createdByName(names.get(item.getCreatedById()));
                    }
                    if (item.getUpdatedById() != null) {
                        builder.updatedByName(names.get(item.getUpdatedById()));
                    }
                    return builder.build();
                })
                .collect(Collectors.toList());

        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    }

    private void enrichSingle(AppointmentReferralResponse response) {
        if (response == null) {
            return;
        }
        var ids = java.util.stream.Stream.of(response.getCreatedById(), response.getUpdatedById())
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return;
        }
        var names = userDirectoryService.getDisplayNames(ids);
        if (response.getCreatedById() != null) {
            response.setCreatedByName(names.get(response.getCreatedById()));
        }
        if (response.getUpdatedById() != null) {
            response.setUpdatedByName(names.get(response.getUpdatedById()));
        }
    }
}


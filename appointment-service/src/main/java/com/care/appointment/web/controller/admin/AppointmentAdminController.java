package com.care.appointment.web.controller.admin;

import com.care.appointment.application.appointment.command.CancelAppointmentCommand;
import com.care.appointment.application.appointment.command.TransferAppointmentCommand;
import com.care.appointment.application.appointment.command.UpdateAppointmentStatusCommand;
import com.care.appointment.domain.model.Appointment;
import com.care.appointment.domain.ports.in.appointment.ManageAppointmentUseCase;
import com.care.appointment.domain.ports.in.appointment.ViewAppointmentUseCase;
import com.care.appointment.infrastructure.db.config.AppointmentFilterConfig;
import com.care.appointment.infrastructure.db.entities.AppointmentEntity;
import com.care.appointment.infrastructure.db.entities.AppointmentStatusHistoryEntity;
import com.care.appointment.infrastructure.db.repositories.AppointmentStatusHistoryRepository;
import com.care.appointment.web.dto.admin.appointment.AppointmentDetailsResponse;
import com.care.appointment.web.dto.admin.appointment.CancelAppointmentRequest;
import com.care.appointment.web.dto.admin.appointment.TransferAppointmentRequest;
import com.care.appointment.web.dto.admin.appointment.UpdateAppointmentStatusRequest;
import com.care.appointment.web.mapper.AppointmentAdminWebMapper;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for Appointment Administration
 *
 * Manages appointments from admin perspective.
 * Provides operations for viewing, updating status, transferring, and cancelling appointments.
 *
 * Features:
 * - View all appointments with advanced filtering
 * - View appointment details
 * - Update appointment status
 * - Transfer appointments between centers
 * - Cancel appointments with reason
 * - View appointment history/audit trail
 * - Statistics and reports
 *
 * All operations are logged in status history table.
 */
@RestController
@RequestMapping({"/api/admin/appointments", "/api/admin/Appointments"})
@RequiredArgsConstructor
@Tag(name = "Appointment Administration", description = "APIs for managing appointments (admin operations)")
public class AppointmentAdminController {

    private final ViewAppointmentUseCase viewAppointmentUseCase;
    private final ManageAppointmentUseCase manageAppointmentUseCase;
    private final AppointmentAdminWebMapper mapper;
    private final AppointmentStatusHistoryRepository statusHistoryRepository;

    /**
     * Get appointment by ID with full details
     * Returns 404 if not found
     */
    @GetMapping("/{appointmentId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Get appointment by ID", description = "Retrieves full appointment details")
    @ApiResponse(responseCode = "200", description = "Appointment found",
            content = @Content(schema = @Schema(implementation = AppointmentDetailsResponse.class)))
    @ApiResponse(responseCode = "404", description = "Appointment not found")
    public ResponseEntity<AppointmentDetailsResponse> getAppointmentById(@PathVariable UUID appointmentId) {
        Optional<Appointment> appointment = viewAppointmentUseCase.getAppointmentById(appointmentId);
        return appointment.map(mapper::toDetailsResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get all appointments with pagination
     * Default page size is 20, sorted by appointment date & time
     */
    @GetMapping
    @Operation(summary = "Get all appointments", description = "Retrieves all appointments with pagination support")
    @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully")
    public ResponseEntity<Page<AppointmentDetailsResponse>> getAllAppointments(@PageableDefault(size = 20) Pageable pageable) {
        FilterRequest safe = new FilterRequest();
        Page<Appointment> appointments = viewAppointmentUseCase.getAllAppointments(safe, pageable);
        Page<AppointmentDetailsResponse> responsePage = appointments.map(mapper::toDetailsResponse);
        return ResponseEntity.ok(responsePage);
    }

    /**
     * Advanced filtering with dynamic criteria
     * Filter by date range, status, center, beneficiary, etc.
     */
    @PostMapping(
            value = "/filter",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Filter appointments", description = "Filters appointments with advanced criteria and pagination")
    @ApiResponse(responseCode = "200", description = "Appointments filtered successfully")
    public ResponseEntity<Page<AppointmentDetailsResponse>> filterAppointments(
            @RequestBody(required = false) FilterRequest request,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        FilterRequest safe = (request != null) ? request : new FilterRequest();
        Page<AppointmentDetailsResponse> page = viewAppointmentUseCase
                .getAllAppointments(safe, pageable)
                .map(mapper::toDetailsResponse);
        return ResponseEntity.ok(page);
    }


    /**
     * Update appointment status
     * Creates history entry automatically
     */
    @PutMapping("/{appointmentId:[0-9a-fA-F\\-]{36}}/status")
    @Operation(summary = "Update appointment status", description = "Updates the status of an appointment")
    @ApiResponse(responseCode = "200", description = "Status updated successfully",
            content = @Content(schema = @Schema(implementation = AppointmentDetailsResponse.class)))
    public ResponseEntity<AppointmentDetailsResponse> updateAppointmentStatus(
            @PathVariable UUID appointmentId,
            @Valid @RequestBody UpdateAppointmentStatusRequest request) {

        UpdateAppointmentStatusCommand command = mapper.toUpdateStatusCommand(appointmentId, request);
        Appointment updated = manageAppointmentUseCase.updateStatus(command);
        return ResponseEntity.ok(mapper.toDetailsResponse(updated));
    }

    /**
     * Cancel appointment
     * Sets cancellation timestamp and reason
     */
    @PostMapping("/{appointmentId:[0-9a-fA-F\\-]{36}}/cancel")
    @Operation(summary = "Cancel appointment", description = "Cancels an appointment with reason")
    @ApiResponse(responseCode = "200", description = "Appointment cancelled successfully",
            content = @Content(schema = @Schema(implementation = AppointmentDetailsResponse.class)))
    public ResponseEntity<AppointmentDetailsResponse> cancelAppointment(
            @PathVariable UUID appointmentId,
            @Valid @RequestBody CancelAppointmentRequest request) {

        CancelAppointmentCommand command = mapper.toCancelCommand(appointmentId, request);
        Appointment cancelled = manageAppointmentUseCase.cancelAppointment(command);
        return ResponseEntity.ok(mapper.toDetailsResponse(cancelled));
    }

    /**
     * Transfer appointment to another center
     * Creates transfer history and updates appointment
     */
    @PostMapping("/{appointmentId:[0-9a-fA-F\\-]{36}}/transfer")
    @Operation(summary = "Transfer appointment", description = "Transfers appointment to another center/branch")
    @ApiResponse(responseCode = "200", description = "Appointment transferred successfully",
            content = @Content(schema = @Schema(implementation = AppointmentDetailsResponse.class)))
    public ResponseEntity<AppointmentDetailsResponse> transferAppointment(
            @PathVariable UUID appointmentId,
            @Valid @RequestBody TransferAppointmentRequest request) {

        TransferAppointmentCommand command = mapper.toTransferCommand(appointmentId, request);
        Appointment transferred = manageAppointmentUseCase.transferAppointment(command);
        return ResponseEntity.ok(mapper.toDetailsResponse(transferred));
    }

    /**
     * Get appointment status history
     * Returns all status changes for an appointment
     */
    @GetMapping("/{appointmentId:[0-9a-fA-F\\-]{36}}/history")
    @Operation(summary = "Get appointment history", description = "Retrieves the complete status history of an appointment")
    @ApiResponse(responseCode = "200", description = "History retrieved successfully")
    public ResponseEntity<List<AppointmentStatusHistoryEntity>> getAppointmentHistory(@PathVariable UUID appointmentId) {
        List<AppointmentStatusHistoryEntity> history = statusHistoryRepository
                .findByAppointmentIdOrderByChangedAtDesc(appointmentId);
        return ResponseEntity.ok(history);
    }
}


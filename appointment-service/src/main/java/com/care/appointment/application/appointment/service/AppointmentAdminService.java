package com.care.appointment.application.appointment.service;

import com.care.appointment.application.appointment.command.CancelAppointmentCommand;
import com.care.appointment.application.appointment.command.TransferAppointmentCommand;
import com.care.appointment.application.appointment.command.UpdateAppointmentStatusCommand;
import com.care.appointment.domain.model.Appointment;
import com.care.appointment.domain.ports.in.appointment.ManageAppointmentUseCase;
import com.care.appointment.domain.ports.in.appointment.ViewAppointmentUseCase;
import com.care.appointment.domain.ports.out.appointment.AppointmentCrudPort;
import com.care.appointment.domain.ports.out.appointment.AppointmentSearchPort;
import com.care.appointment.infrastructure.db.entities.AppointmentTransferEntity;
import com.care.appointment.infrastructure.db.repositories.AppointmentTransferRepository;
import com.sharedlib.core.filter.FilterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppointmentAdminService implements ViewAppointmentUseCase, ManageAppointmentUseCase {

    private final AppointmentCrudPort appointmentCrudPort;
    private final AppointmentSearchPort appointmentSearchPort;
    private final AppointmentTransferRepository transferRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Appointment> getAppointmentById(UUID appointmentId) {
        log.debug("Loading appointment by ID: {}", appointmentId);
        return appointmentCrudPort.findById(appointmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Appointment> getAllAppointments(FilterRequest filter, Pageable pageable) {
        log.debug("Loading all appointments with filter and pagination");
        return appointmentSearchPort.search(filter, pageable);
    }

    @Override
    public Appointment updateStatus(UpdateAppointmentStatusCommand command) {
        log.info("Updating appointment status: {}", command.getAppointmentId());

        Appointment appointment = appointmentCrudPort.findById(command.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + command.getAppointmentId()));

        appointment.setAppointmentStatusId(command.getAppointmentStatusId());
        if (command.getNotes() != null) {
            appointment.setNotes(command.getNotes());
        }
        appointment.setUpdatedById(command.getUpdatedById());

        Appointment updated = appointmentCrudPort.update(appointment);
        log.info("Appointment status updated successfully: {}", updated.getAppointmentId());
        return updated;
    }

    @Override
    public Appointment cancelAppointment(CancelAppointmentCommand command) {
        log.info("Cancelling appointment: {}", command.getAppointmentId());

        Appointment appointment = appointmentCrudPort.findById(command.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + command.getAppointmentId()));

        if (appointment.getCancelledAt() != null) {
            throw new IllegalArgumentException("Appointment is already cancelled");
        }

        appointment.setCancelledAt(Instant.now());
        appointment.setCancellationReason(command.getCancellationReason());
        appointment.setUpdatedById(command.getCancelledById());

        Appointment cancelled = appointmentCrudPort.update(appointment);
        log.info("Appointment cancelled successfully: {}", cancelled.getAppointmentId());
        return cancelled;
    }

    @Override
    public Appointment transferAppointment(TransferAppointmentCommand command) {
        log.info("Transferring appointment: {} to branch: {}", 
                command.getAppointmentId(), command.getTargetOrganizationBranchId());

        Appointment appointment = appointmentCrudPort.findById(command.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + command.getAppointmentId()));

        if (appointment.getCancelledAt() != null) {
            throw new IllegalArgumentException("Cannot transfer cancelled appointment");
        }

        // Save transfer history
        AppointmentTransferEntity transfer = AppointmentTransferEntity.builder()
                .appointmentId(command.getAppointmentId())
                .fromOrganizationBranchId(appointment.getOrganizationBranchId())
                .toOrganizationBranchId(command.getTargetOrganizationBranchId())
                .transferReason(command.getTransferReason())
                .transferredAt(Instant.now())
                .transferredByUserId(command.getTransferredById())
                .build();
        transferRepository.save(transfer);

        // Update appointment
        appointment.setOrganizationBranchId(command.getTargetOrganizationBranchId());
        appointment.setAppointmentDate(command.getNewAppointmentDate());
        appointment.setAppointmentTime(command.getNewAppointmentTime());
        appointment.setUpdatedById(command.getTransferredById());

        Appointment transferred = appointmentCrudPort.update(appointment);
        log.info("Appointment transferred successfully: {}", transferred.getAppointmentId());
        return transferred;
    }
}


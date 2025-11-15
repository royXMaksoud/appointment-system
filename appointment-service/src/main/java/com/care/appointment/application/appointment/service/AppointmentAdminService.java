package com.care.appointment.application.appointment.service;

import com.care.appointment.application.appointment.command.CancelAppointmentCommand;
import com.care.appointment.application.appointment.command.CreateAppointmentCommand;
import com.care.appointment.application.appointment.command.TransferAppointmentCommand;
import com.care.appointment.application.appointment.command.UpdateAppointmentCommand;
import com.care.appointment.application.appointment.command.UpdateAppointmentStatusCommand;
import com.care.appointment.application.service.AppointmentCodeGeneratorService;
import com.care.appointment.domain.model.Appointment;
import com.care.appointment.domain.ports.in.appointment.ManageAppointmentUseCase;
import com.care.appointment.domain.ports.in.appointment.ViewAppointmentUseCase;
import com.care.appointment.domain.ports.out.appointment.AppointmentCrudPort;
import com.care.appointment.domain.ports.out.appointment.AppointmentSearchPort;
import com.care.appointment.infrastructure.client.AccessManagementClient;
import com.care.appointment.infrastructure.db.entities.AppointmentStatusEntity;
import com.care.appointment.infrastructure.db.entities.AppointmentTransferEntity;
import com.care.appointment.infrastructure.db.repositories.AppointmentReferralRepository;
import com.care.appointment.infrastructure.db.repositories.AppointmentStatusHistoryRepository;
import com.care.appointment.infrastructure.db.repositories.AppointmentStatusRepository;
import com.care.appointment.infrastructure.db.repositories.AppointmentTransferRepository;
import com.care.appointment.web.dto.OrganizationBranchDTO;
import com.sharedlib.core.filter.FilterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
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
    private final AppointmentStatusRepository appointmentStatusRepository;
    private final AppointmentReferralRepository appointmentReferralRepository;
    private final AppointmentStatusHistoryRepository appointmentStatusHistoryRepository;
    private final AppointmentCodeGeneratorService codeGeneratorService;
    private final AccessManagementClient accessManagementClient;

    private static final String CANCELLED_STATUS_CODE = "CAN";

    @Override
    public Appointment createAppointment(CreateAppointmentCommand command) {
        log.info("Creating new appointment for beneficiary: {}, branch: {}, date: {}, time: {}",
                command.getBeneficiaryId(), command.getOrganizationBranchId(),
                command.getAppointmentDate(), command.getAppointmentTime());

        // Fetch branch code for appointment code generation
        String branchCode = "UNKNOWN";
        try {
            OrganizationBranchDTO branchDTO = accessManagementClient.getOrganizationBranch(command.getOrganizationBranchId());
            if (branchDTO != null && branchDTO.getCode() != null) {
                branchCode = branchDTO.getCode();
            }
        } catch (Exception ex) {
            log.warn("Failed to fetch branch code for appointment code generation, using default: {}", ex.getMessage());
        }

        // Generate unique appointment code
        String appointmentCode = codeGeneratorService.generateAppointmentCode(
                command.getOrganizationBranchId(),
                branchCode
        );

        Appointment appointment = Appointment.builder()
                .appointmentRequestId(command.getAppointmentRequestId())
                .beneficiaryId(command.getBeneficiaryId())
                .organizationBranchId(command.getOrganizationBranchId())
                .serviceTypeId(command.getServiceTypeId())
                .appointmentDate(command.getAppointmentDate())
                .appointmentTime(command.getAppointmentTime())
                .slotDurationMinutes(command.getSlotDurationMinutes())
                .appointmentStatusId(command.getAppointmentStatusId())
                .priority(command.getPriority() != null ? command.getPriority() : "NORMAL")
                .notes(command.getNotes())
                .actionTypeId(command.getActionTypeId())
                .actionNotes(command.getActionNotes())
                .attendedAt(command.getAttendedAt())
                .completedAt(command.getCompletedAt())
                .cancelledAt(command.getCancelledAt())
                .cancellationReason(command.getCancellationReason())
                .createdById(command.getCreatedById())
                .appointmentCode(appointmentCode)
                .build();

        Appointment saved = appointmentCrudPort.save(appointment);
        log.info("Appointment created successfully: {} with code: {}", saved.getAppointmentId(), appointmentCode);
        return saved;
    }

    @Override
    public Appointment updateAppointment(UpdateAppointmentCommand command) {
        log.info("Updating appointment: {}", command.getAppointmentId());

        Appointment existing = appointmentCrudPort.findById(command.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + command.getAppointmentId()));

        existing.setAppointmentRequestId(command.getAppointmentRequestId());
        existing.setBeneficiaryId(command.getBeneficiaryId());
        existing.setOrganizationBranchId(command.getOrganizationBranchId());
        existing.setServiceTypeId(command.getServiceTypeId());
        existing.setAppointmentDate(command.getAppointmentDate());
        existing.setAppointmentTime(command.getAppointmentTime());
        if (command.getSlotDurationMinutes() != null) {
            existing.setSlotDurationMinutes(command.getSlotDurationMinutes());
        }
        if (command.getAppointmentStatusId() != null) {
            existing.setAppointmentStatusId(command.getAppointmentStatusId());
        }
        if (command.getPriority() != null) {
            existing.setPriority(command.getPriority());
        }
        existing.setNotes(command.getNotes());
        existing.setActionTypeId(command.getActionTypeId());
        existing.setActionNotes(command.getActionNotes());
        existing.setAttendedAt(command.getAttendedAt());
        existing.setCompletedAt(command.getCompletedAt());
        existing.setCancelledAt(command.getCancelledAt());
        existing.setCancellationReason(command.getCancellationReason());
        existing.setUpdatedById(command.getUpdatedById());

        Appointment updated = appointmentCrudPort.update(existing);
        log.info("Appointment updated successfully: {}", updated.getAppointmentId());
        return updated;
    }

    @Override
    public void deleteAppointment(UUID appointmentId) {
        log.info("Deleting appointment: {}", appointmentId);
        appointmentCrudPort.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

        // Delete dependent records to avoid FK violations
        appointmentReferralRepository.deleteByAppointmentId(appointmentId);
        log.info("Deleted referral records for appointment {}", appointmentId);

        transferRepository.deleteByAppointmentId(appointmentId);
        log.info("Deleted transfer records for appointment {}", appointmentId);

        appointmentStatusHistoryRepository.deleteByAppointmentId(appointmentId);
        log.info("Deleted status history records for appointment {}", appointmentId);

        appointmentCrudPort.deleteById(appointmentId);
        log.info("Appointment deleted successfully: {}", appointmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Appointment> getAppointmentById(UUID appointmentId) {
        log.debug("Loading appointment by ID: {}", appointmentId);
        return appointmentCrudPort.findById(appointmentId);
    }

    @Transactional(readOnly = true)
    public Optional<Appointment> getAppointmentByCode(String appointmentCode) {
        if (appointmentCode == null || appointmentCode.isBlank()) {
            return Optional.empty();
        }
        log.debug("Loading appointment by code: {}", appointmentCode);
        return appointmentCrudPort.findByAppointmentCode(appointmentCode);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Appointment> getAllAppointments(FilterRequest filter, Pageable pageable) {
        log.debug("Loading all appointments with filter and pagination");
        return appointmentSearchPort.search(filter, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Appointment> getAppointmentsByBeneficiary(UUID beneficiaryId, Pageable pageable) {
        log.debug("Loading appointments for beneficiary: {}", beneficiaryId);

        Sort defaultSort = Sort.by(Sort.Direction.DESC, "appointmentDate")
                .and(Sort.by(Sort.Direction.DESC, "appointmentTime"));

        Pageable safePageable;
        if (pageable == null || !pageable.isPaged()) {
            safePageable = PageRequest.of(0, 20, defaultSort);
        } else {
            Sort effectiveSort = pageable.getSort().isSorted() ? pageable.getSort() : defaultSort;
            safePageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), effectiveSort);
        }

        return appointmentSearchPort.findByBeneficiaryId(beneficiaryId, safePageable);
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
        appointment.setAppointmentStatusId(resolveStatusIdByCode(CANCELLED_STATUS_CODE));

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
        AppointmentTransferEntity transfer = Objects.requireNonNull(AppointmentTransferEntity.builder()
                .appointmentId(command.getAppointmentId())
                .fromOrganizationBranchId(appointment.getOrganizationBranchId())
                .toOrganizationBranchId(command.getTargetOrganizationBranchId())
                .transferReason(command.getTransferReason())
                .transferredAt(Instant.now())
                .transferredByUserId(command.getTransferredById())
                .build());
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

    private UUID resolveStatusIdByCode(String code) {
        return appointmentStatusRepository.findByCodeIgnoreCaseAndIsDeletedFalse(code)
                .map(AppointmentStatusEntity::getAppointmentStatusId)
                .orElseThrow(() -> new IllegalStateException("Appointment status code '" + code + "' is not configured"));
    }
}


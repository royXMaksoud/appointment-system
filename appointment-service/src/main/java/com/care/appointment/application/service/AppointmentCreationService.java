package com.care.appointment.application.service;

import com.care.appointment.application.dto.AppointmentCreateRequest;
import com.care.appointment.domain.model.Appointment;
import com.care.appointment.infrastructure.db.entities.AppointmentEntity;
import com.care.appointment.infrastructure.db.repositories.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Service for creating appointments with QR code and verification code generation
 * Automatically generates and persists:
 * - Appointment Code (YEAR-CENTER-SEQUENCE)
 * - QR Code URL (PNG image)
 * - Verification Code (3-digit format: 4-2-7)
 * - Verification Code Expiry
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentCreationService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentCodeGeneratorService codeGeneratorService;
    private final QRCodeGeneratorService qrCodeGeneratorService;

    /**
     * Create appointment with QR code and verification code
     *
     * @param appointment Appointment domain object
     * @return Created appointment with generated codes
     */
    @Transactional
    public Appointment createAppointmentWithQR(Appointment appointment) {
        log.info("Creating appointment for beneficiary: {} at branch: {}",
            appointment.getBeneficiaryId(), appointment.getOrganizationBranchId());

        // Generate appointment code (YEAR-CENTER-SEQUENCE)
        // Using branch ID as code since OrganizationBranch is managed by reference-data-service
        String branchCode = "BRANCH-" + appointment.getOrganizationBranchId().toString().substring(0, 8).toUpperCase();
        String appointmentCode = codeGeneratorService.generateAppointmentCode(
            appointment.getOrganizationBranchId(),
            branchCode
        );
        log.info("Generated appointment code: {}", appointmentCode);

        // Generate verification code (3 digits like "4-2-7")
        String verificationCode = qrCodeGeneratorService.generateVerificationCode();
        log.info("Generated verification code for appointment: {}", appointmentCode);

        // Generate QR code with both code and UUID
        String qrCodeUrl = qrCodeGeneratorService.generateQRCode(appointmentCode, appointment.getAppointmentId());
        log.info("Generated QR code for appointment: {}", appointmentCode);

        // Set the generated values on appointment
        appointment.setAppointmentCode(appointmentCode);
        appointment.setQrCodeUrl(qrCodeUrl);
        appointment.setVerificationCode(verificationCode);
        // Verification code expires in 24 hours
        appointment.setVerificationCodeExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));

        // Convert to entity and save
        AppointmentEntity entity = mapToEntity(appointment);
        AppointmentEntity saved = appointmentRepository.save(entity);

        log.info("Appointment created successfully with code: {} and verification: {}",
            appointmentCode, verificationCode);

        return mapToDomain(saved);
    }

    /**
     * Create appointment from DTO/request with full QR generation
     */
    @Transactional
    public Appointment createAppointment(AppointmentCreateRequest request) {
        Appointment appointment = Appointment.builder()
            .beneficiaryId(request.getBeneficiaryId())
            .organizationBranchId(request.getOrganizationBranchId())
            .serviceTypeId(request.getServiceTypeId())
            .appointmentDate(request.getAppointmentDate())
            .appointmentTime(request.getAppointmentTime())
            .slotDurationMinutes(request.getSlotDurationMinutes() != null ? request.getSlotDurationMinutes() : 30)
            .appointmentStatusId(request.getAppointmentStatusId())
            .priority(request.getPriority() != null ? request.getPriority() : "NORMAL")
            .notes(request.getNotes())
            .createdById(request.getCreatedById())
            .createdAt(Instant.now())
            .build();

        return createAppointmentWithQR(appointment);
    }

    /**
     * Map Appointment domain to AppointmentEntity
     */
    private AppointmentEntity mapToEntity(Appointment appointment) {
        return AppointmentEntity.builder()
            .appointmentId(appointment.getAppointmentId())
            .appointmentRequestId(appointment.getAppointmentRequestId())
            .beneficiaryId(appointment.getBeneficiaryId())
            .organizationBranchId(appointment.getOrganizationBranchId())
            .serviceTypeId(appointment.getServiceTypeId())
            .appointmentDate(appointment.getAppointmentDate())
            .appointmentTime(appointment.getAppointmentTime())
            .slotDurationMinutes(appointment.getSlotDurationMinutes())
            .appointmentStatusId(appointment.getAppointmentStatusId())
            .priority(appointment.getPriority())
            .notes(appointment.getNotes())
            .actionTypeId(appointment.getActionTypeId())
            .actionNotes(appointment.getActionNotes())
            .attendedAt(appointment.getAttendedAt())
            .completedAt(appointment.getCompletedAt())
            .cancelledAt(appointment.getCancelledAt())
            .cancellationReason(appointment.getCancellationReason())
            .createdById(appointment.getCreatedById())
            .createdAt(appointment.getCreatedAt())
            .updatedById(appointment.getUpdatedById())
            .updatedAt(appointment.getUpdatedAt())
            .appointmentCode(appointment.getAppointmentCode())
            .qrCodeUrl(appointment.getQrCodeUrl())
            .verificationCode(appointment.getVerificationCode())
            .verificationCodeExpiresAt(appointment.getVerificationCodeExpiresAt())
            .build();
    }

    /**
     * Map AppointmentEntity to domain
     */
    private Appointment mapToDomain(AppointmentEntity entity) {
        return Appointment.builder()
            .appointmentId(entity.getAppointmentId())
            .appointmentRequestId(entity.getAppointmentRequestId())
            .beneficiaryId(entity.getBeneficiaryId())
            .organizationBranchId(entity.getOrganizationBranchId())
            .serviceTypeId(entity.getServiceTypeId())
            .appointmentDate(entity.getAppointmentDate())
            .appointmentTime(entity.getAppointmentTime())
            .slotDurationMinutes(entity.getSlotDurationMinutes())
            .appointmentStatusId(entity.getAppointmentStatusId())
            .priority(entity.getPriority())
            .notes(entity.getNotes())
            .actionTypeId(entity.getActionTypeId())
            .actionNotes(entity.getActionNotes())
            .attendedAt(entity.getAttendedAt())
            .completedAt(entity.getCompletedAt())
            .cancelledAt(entity.getCancelledAt())
            .cancellationReason(entity.getCancellationReason())
            .createdById(entity.getCreatedById())
            .createdAt(entity.getCreatedAt())
            .updatedById(entity.getUpdatedById())
            .updatedAt(entity.getUpdatedAt())
            .appointmentCode(entity.getAppointmentCode())
            .qrCodeUrl(entity.getQrCodeUrl())
            .verificationCode(entity.getVerificationCode())
            .verificationCodeExpiresAt(entity.getVerificationCodeExpiresAt())
            .build();
    }
}

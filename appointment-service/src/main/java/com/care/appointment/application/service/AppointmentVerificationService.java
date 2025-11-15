package com.care.appointment.application.service;

import com.care.appointment.application.dto.AppointmentQRDTO;
import com.care.appointment.application.dto.VerifyAppointmentResponse;
import com.care.appointment.infrastructure.db.entities.AppointmentEntity;
import com.care.appointment.infrastructure.db.repositories.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Service for verifying appointments using QR code, appointment code, or verification code
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentVerificationService {

    private final AppointmentRepository appointmentRepository;
    private final QRCodeGeneratorService qrCodeGeneratorService;

    /**
     * Verify appointment by appointment code (e.g., HQ-2025-0001)
     */
    @Transactional(readOnly = true)
    public VerifyAppointmentResponse verifyByAppointmentCode(String appointmentCode) {
        log.info("Verifying appointment by code: {}", appointmentCode);

        AppointmentEntity appointment = appointmentRepository.findByAppointmentCode(appointmentCode)
            .orElse(null);

        if (appointment == null) {
            return VerifyAppointmentResponse.builder()
                .success(false)
                .message("Appointment not found with code: " + appointmentCode)
                .errorCode("NOT_FOUND")
                .build();
        }

        return buildSuccessResponse(appointment, "Appointment verified successfully");
    }

    /**
     * Verify appointment using verification code (3-digit code)
     */
    @Transactional(readOnly = true)
    public VerifyAppointmentResponse verifyByVerificationCode(String appointmentCode, String verificationCode) {
        log.info("Verifying appointment {} with verification code", appointmentCode);

        AppointmentEntity appointment = appointmentRepository.findByAppointmentCode(appointmentCode)
            .orElse(null);

        if (appointment == null) {
            return VerifyAppointmentResponse.builder()
                .success(false)
                .message("Appointment not found")
                .errorCode("NOT_FOUND")
                .build();
        }

        // Check if verification code is expired
        if (appointment.getVerificationCodeExpiresAt() != null &&
            Instant.now().isAfter(appointment.getVerificationCodeExpiresAt())) {
            return VerifyAppointmentResponse.builder()
                .success(false)
                .message("Verification code has expired")
                .errorCode("EXPIRED")
                .build();
        }

        // Verify the code
        if (!qrCodeGeneratorService.verifyCode(verificationCode, appointment.getVerificationCode())) {
            return VerifyAppointmentResponse.builder()
                .success(false)
                .message("Invalid verification code")
                .errorCode("INVALID_CODE")
                .build();
        }

        return buildSuccessResponse(appointment, "Appointment verified successfully");
    }

    /**
     * Get QR code data for an appointment
     */
    @Transactional(readOnly = true)
    public AppointmentQRDTO getAppointmentQRData(UUID appointmentId) {
        log.info("Retrieving QR data for appointment: {}", appointmentId);

        AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
            .orElse(null);

        if (appointment == null) {
            throw new RuntimeException("Appointment not found with ID: " + appointmentId);
        }

        return mapToAppointmentQRDTO(appointment);
    }

    /**
     * Verify by QR content (format: APPT:CODE|ID:UUID)
     */
    @Transactional(readOnly = true)
    public VerifyAppointmentResponse verifyByQRContent(String qrContent) {
        log.info("Verifying appointment by QR content");

        // Parse QR content: APPT:HQ-2025-0001|ID:uuid
        try {
            String appointmentCode = extractFromQR(qrContent, "APPT");
            String appointmentId = extractFromQR(qrContent, "ID");

            // Verify the code exists
            AppointmentEntity appointment = appointmentRepository.findByAppointmentCode(appointmentCode)
                .orElse(null);

            if (appointment == null) {
                return VerifyAppointmentResponse.builder()
                    .success(false)
                    .message("Appointment not found")
                    .errorCode("NOT_FOUND")
                    .build();
            }

            // Verify ID matches
            if (!appointment.getAppointmentId().toString().equals(appointmentId)) {
                return VerifyAppointmentResponse.builder()
                    .success(false)
                    .message("Appointment ID mismatch")
                    .errorCode("INVALID_CODE")
                    .build();
            }

            return buildSuccessResponse(appointment, "QR code verified successfully");

        } catch (Exception e) {
            log.error("Error parsing QR content", e);
            return VerifyAppointmentResponse.builder()
                .success(false)
                .message("Invalid QR code format")
                .errorCode("INVALID_FORMAT")
                .build();
        }
    }

    /**
     * Get QR code as PNG image bytes
     */
    public byte[] getQRCodeImage(UUID appointmentId) {
        AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
            .orElse(null);

        if (appointment == null) {
            throw new RuntimeException("Appointment not found");
        }

        // If QR code URL is a base64 data URL, extract the PNG bytes
        String qrUrl = appointment.getQrCodeUrl();
        if (qrUrl != null && qrUrl.startsWith("data:image/png;base64,")) {
            String base64Data = qrUrl.substring("data:image/png;base64,".length());
            return java.util.Base64.getDecoder().decode(base64Data);
        }

        // Generate fresh QR if not stored
        String qrUrl_generated = qrCodeGeneratorService.generateQRCode(
            appointment.getAppointmentCode(),
            appointment.getAppointmentId()
        );

        String base64Data = qrUrl_generated.substring("data:image/png;base64,".length());
        return java.util.Base64.getDecoder().decode(base64Data);
    }

    /**
     * Resend QR code to beneficiary via SMS, Email, or Push
     */
    @Transactional
    public void resendQRCode(UUID appointmentId, String method) {
        AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
            .orElse(null);

        if (appointment == null) {
            throw new RuntimeException("Appointment not found");
        }

        // TODO: Integrate with SMS/Email/Push notification service
        log.info("Resending QR code for appointment {} via {}", appointmentId, method);
    }

    /**
     * Build success response with appointment QR data
     */
    private VerifyAppointmentResponse buildSuccessResponse(AppointmentEntity appointment, String message) {
        return VerifyAppointmentResponse.builder()
            .success(true)
            .message(message)
            .appointmentId(appointment.getAppointmentId())
            .appointmentCode(appointment.getAppointmentCode())
            .verifiedAt(Instant.now())
            .appointment(mapToAppointmentQRDTO(appointment))
            .build();
    }

    /**
     * Map AppointmentEntity to AppointmentQRDTO
     */
    private AppointmentQRDTO mapToAppointmentQRDTO(AppointmentEntity appointment) {
        return AppointmentQRDTO.builder()
            .appointmentId(appointment.getAppointmentId())
            .appointmentCode(appointment.getAppointmentCode())
            .qrCodeUrl(appointment.getQrCodeUrl())
            .verificationCode(appointment.getVerificationCode())
            .verificationCodeExpiresAt(appointment.getVerificationCodeExpiresAt())
            .appointmentDate(appointment.getAppointmentDate().toString())
            .appointmentTime(appointment.getAppointmentTime().toString())
            .build();
    }

    /**
     * Extract value from QR content
     */
    private String extractFromQR(String qrContent, String key) {
        String[] parts = qrContent.split("\\|");
        for (String part : parts) {
            if (part.startsWith(key + ":")) {
                return part.substring((key + ":").length());
            }
        }
        throw new IllegalArgumentException("Key not found in QR content: " + key);
    }
}

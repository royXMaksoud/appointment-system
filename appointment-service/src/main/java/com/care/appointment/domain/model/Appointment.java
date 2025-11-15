package com.care.appointment.domain.model;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    private UUID appointmentId;
    private UUID appointmentRequestId;
    private UUID beneficiaryId;
    private UUID organizationBranchId;
    private UUID serviceTypeId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private Integer slotDurationMinutes;
    private UUID appointmentStatusId;
    private String priority;
    private String notes;
    private UUID actionTypeId;
    private String actionNotes;
    private Instant attendedAt;
    private Instant completedAt;
    private Instant cancelledAt;
    private String cancellationReason;
    private UUID createdById;
    private Instant createdAt;
    private UUID updatedById;
    private Instant updatedAt;
    private Long rowVersion;

    // QR Code & Tracking
    private String appointmentCode;      // e.g., HQ-2025-0001
    private String qrCodeUrl;            // URL to QR code image
    private String verificationCode;     // e.g., "4-2-7"
    private Instant verificationCodeExpiresAt;
}


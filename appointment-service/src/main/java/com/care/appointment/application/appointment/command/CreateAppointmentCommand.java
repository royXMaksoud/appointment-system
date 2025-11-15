package com.care.appointment.application.appointment.command;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentCommand {
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
}


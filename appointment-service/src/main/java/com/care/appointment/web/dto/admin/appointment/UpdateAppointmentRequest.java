package com.care.appointment.web.dto.admin.appointment;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentRequest {

    private UUID appointmentRequestId;

    @NotNull(message = "Beneficiary ID is required")
    private UUID beneficiaryId;

    @NotNull(message = "Organization branch ID is required")
    private UUID organizationBranchId;

    @NotNull(message = "Service type ID is required")
    private UUID serviceTypeId;

    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;

    @NotNull(message = "Appointment time is required")
    private LocalTime appointmentTime;

    @Min(value = 5, message = "Slot duration must be at least 5 minutes")
    @Max(value = 480, message = "Slot duration must not exceed 480 minutes")
    private Integer slotDurationMinutes;

    private UUID appointmentStatusId;

    @Pattern(regexp = "NORMAL|URGENT", message = "Priority must be NORMAL or URGENT")
    private String priority;

    @Size(max = 4000, message = "Notes must not exceed 4000 characters")
    private String notes;

    private UUID actionTypeId;

    @Size(max = 4000, message = "Action notes must not exceed 4000 characters")
    private String actionNotes;

    private Instant attendedAt;
    private Instant completedAt;
    private Instant cancelledAt;

    @Size(max = 2000, message = "Cancellation reason must not exceed 2000 characters")
    private String cancellationReason;

    private UUID updatedById;
}


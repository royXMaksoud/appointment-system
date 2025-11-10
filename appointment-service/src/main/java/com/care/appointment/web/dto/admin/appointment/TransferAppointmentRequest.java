package com.care.appointment.web.dto.admin.appointment;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferAppointmentRequest {

    @NotNull(message = "Target branch ID is required")
    private UUID targetOrganizationBranchId;

    @NotNull(message = "New appointment date is required")
    private LocalDate newAppointmentDate;

    @NotNull(message = "New appointment time is required")
    private LocalTime newAppointmentTime;

    @NotBlank(message = "Transfer reason is required")
    @Size(max = 1000, message = "Transfer reason must not exceed 1000 characters")
    private String transferReason;

    private UUID transferredById;
}


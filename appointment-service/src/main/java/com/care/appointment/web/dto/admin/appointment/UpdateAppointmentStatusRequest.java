package com.care.appointment.web.dto.admin.appointment;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentStatusRequest {

    @NotNull(message = "Status ID is required")
    private UUID appointmentStatusId;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    private UUID updatedById;
}


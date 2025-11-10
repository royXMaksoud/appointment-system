package com.care.appointment.web.dto.admin.appointment;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelAppointmentRequest {

    @NotBlank(message = "Cancellation reason is required")
    @Size(max = 1000, message = "Cancellation reason must not exceed 1000 characters")
    private String cancellationReason;

    private UUID cancelledById;
}


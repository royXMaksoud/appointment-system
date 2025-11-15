package com.care.appointment.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

/**
 * Request DTO for verifying appointment using code or QR
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyAppointmentRequest {

    @NotBlank(message = "Appointment code or ID is required")
    @JsonProperty("appointment_code_or_id")
    private String appointmentCodeOrId;

    @JsonProperty("verification_code")
    private String verificationCode; // 3-digit code like "4-2-7"

    @JsonProperty("method")
    private String method; // QR, CODE, or VERIFICATION_CODE
}

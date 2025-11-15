package com.care.appointment.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO after verifying appointment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyAppointmentResponse {

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("appointment_id")
    private UUID appointmentId;

    @JsonProperty("appointment_code")
    private String appointmentCode;

    @JsonProperty("verified_at")
    private Instant verifiedAt;

    @JsonProperty("appointment")
    private AppointmentQRDTO appointment;

    @JsonProperty("error_code")
    private String errorCode; // INVALID_CODE, EXPIRED, NOT_FOUND, etc.
}

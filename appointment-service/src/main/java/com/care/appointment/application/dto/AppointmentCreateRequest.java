package com.care.appointment.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO for creating new appointments
 * Will automatically generate QR code and verification code
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCreateRequest {

    @NotNull(message = "Beneficiary ID is required")
    @JsonProperty("beneficiary_id")
    private UUID beneficiaryId;

    @NotNull(message = "Organization branch ID is required")
    @JsonProperty("organization_branch_id")
    private UUID organizationBranchId;

    @NotNull(message = "Service type ID is required")
    @JsonProperty("service_type_id")
    private UUID serviceTypeId;

    @NotNull(message = "Appointment date is required")
    @JsonProperty("appointment_date")
    private LocalDate appointmentDate;

    @NotNull(message = "Appointment time is required")
    @JsonProperty("appointment_time")
    private LocalTime appointmentTime;

    @JsonProperty("slot_duration_minutes")
    private Integer slotDurationMinutes;

    @NotNull(message = "Appointment status ID is required")
    @JsonProperty("appointment_status_id")
    private UUID appointmentStatusId;

    @JsonProperty("priority")
    private String priority; // NORMAL or URGENT

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("appointment_request_id")
    private UUID appointmentRequestId; // Optional, if from a request

    @JsonProperty("created_by_user_id")
    private UUID createdById; // Current user ID
}

package com.care.appointment.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain model for appointment status history entries.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatusHistory {

    private UUID historyId;
    private UUID appointmentId;
    private UUID appointmentStatusId;
    private UUID changedByUserId;
    private String reason;
    private Instant changedAt;
}


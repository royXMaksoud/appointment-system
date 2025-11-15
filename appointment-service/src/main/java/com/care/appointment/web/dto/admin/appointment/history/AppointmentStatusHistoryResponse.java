package com.care.appointment.web.dto.admin.appointment.history;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatusHistoryResponse {
    private UUID historyId;
    private UUID appointmentId;
    private UUID appointmentStatusId;
    private UUID changedByUserId;
    private String changedByName;
    private String reason;
    private Instant changedAt;
}


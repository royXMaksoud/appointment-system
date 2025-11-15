package com.care.appointment.web.dto.admin.appointmentstatus;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class AppointmentStatusResponse {
    UUID appointmentStatusId;
    String code;
    String name;
    Boolean isActive;
    Boolean isDeleted;
    Instant createdAt;
    Instant updatedAt;
}



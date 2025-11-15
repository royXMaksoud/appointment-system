package com.care.appointment.web.dto.admin.appointment.history;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentStatusHistoryRequest {

    @NotNull
    private UUID appointmentId;

    @NotNull
    private UUID appointmentStatusId;

    private UUID changedByUserId;

    @Size(max = 1000)
    private String reason;
}


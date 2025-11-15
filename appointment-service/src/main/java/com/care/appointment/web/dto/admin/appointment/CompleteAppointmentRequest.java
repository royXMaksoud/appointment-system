package com.care.appointment.web.dto.admin.appointment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteAppointmentRequest {

    private UUID actionTypeId;

    private String actionNotes;

    @NotNull
    private UUID completedByUserId;
}


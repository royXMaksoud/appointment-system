package com.care.appointment.application.appointment.command;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentStatusCommand {
    private UUID appointmentId;
    private UUID appointmentStatusId;
    private String notes;
    private UUID updatedById;
}


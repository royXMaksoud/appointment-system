package com.care.appointment.application.appointment.command;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelAppointmentCommand {
    private UUID appointmentId;
    private String cancellationReason;
    private UUID cancelledById;
}


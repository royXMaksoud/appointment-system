package com.care.appointment.application.appointment.command;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferAppointmentCommand {
    private UUID appointmentId;
    private UUID targetOrganizationBranchId;
    private LocalDate newAppointmentDate;
    private LocalTime newAppointmentTime;
    private String transferReason;
    private UUID transferredById;
}


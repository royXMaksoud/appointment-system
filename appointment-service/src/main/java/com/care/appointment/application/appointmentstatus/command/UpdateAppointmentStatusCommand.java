package com.care.appointment.application.appointmentstatus.command;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class UpdateAppointmentStatusCommand {
    UUID appointmentStatusId;
    String code;
    String name;
    Boolean isActive;
    Boolean isDeleted;
}



package com.care.appointment.application.appointmentstatus.command;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateAppointmentStatusCommand {
    String code;
    String name;
    Boolean isActive;
}



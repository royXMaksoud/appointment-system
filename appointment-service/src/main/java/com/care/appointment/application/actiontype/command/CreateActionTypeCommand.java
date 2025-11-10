package com.care.appointment.application.actiontype.command;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateActionTypeCommand {
    String name;
    String code;
    String description;
    Boolean isActive;
    Boolean requiresTransfer;
    Boolean completesAppointment;
    String color;
    Integer displayOrder;
}


package com.care.appointment.application.actiontype.command;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class UpdateActionTypeCommand {
    UUID actionTypeId;
    String name;
    String description;
    Boolean isActive;
    Boolean requiresTransfer;
    Boolean completesAppointment;
    String color;
    Integer displayOrder;
}


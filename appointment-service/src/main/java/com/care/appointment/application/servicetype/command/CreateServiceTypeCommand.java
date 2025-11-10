package com.care.appointment.application.servicetype.command;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class CreateServiceTypeCommand {
    String name;
    String description;
    UUID parentId;
    Boolean isActive;
    Boolean isLeaf;
    String code;
    Integer displayOrder;
}


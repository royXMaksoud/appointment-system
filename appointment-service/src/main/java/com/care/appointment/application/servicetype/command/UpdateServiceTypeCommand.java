package com.care.appointment.application.servicetype.command;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class UpdateServiceTypeCommand {
    UUID serviceTypeId;
    String name;
    String description;
    UUID parentId;
    Boolean isActive;
    Boolean isLeaf;
    String code;
    Integer displayOrder;
}


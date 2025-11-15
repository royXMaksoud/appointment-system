package com.care.appointment.application.servicetypelanguage.command;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class UpdateServiceTypeLanguageCommand {
    UUID serviceTypeLanguageId;
    UUID serviceTypeId;
    String languageCode;
    String name;
    String description;
    Boolean isActive;
    Boolean isDeleted;
}



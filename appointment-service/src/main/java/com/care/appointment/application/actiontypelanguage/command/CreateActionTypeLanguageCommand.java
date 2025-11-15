package com.care.appointment.application.actiontypelanguage.command;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class CreateActionTypeLanguageCommand {
    UUID actionTypeId;
    String languageCode;
    String name;
    Boolean isActive;
    Boolean isDeleted;
}



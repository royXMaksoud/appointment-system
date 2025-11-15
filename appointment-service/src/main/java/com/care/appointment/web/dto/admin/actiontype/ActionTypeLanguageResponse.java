package com.care.appointment.web.dto.admin.actiontype;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class ActionTypeLanguageResponse {
    UUID actionTypeLanguageId;
    UUID actionTypeId;
    String languageCode;
    String name;
    Boolean isActive;
    Boolean isDeleted;
    Instant createdAt;
    Instant updatedAt;
    Long rowVersion;
}



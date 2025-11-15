package com.care.appointment.web.dto.admin.servicetype;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class ServiceTypeLanguageResponse {
    UUID serviceTypeLanguageId;
    UUID serviceTypeId;
    String languageCode;
    String name;
    String description;
    Boolean isActive;
    Boolean isDeleted;
    Instant createdAt;
    Instant updatedAt;
    Long rowVersion;
}



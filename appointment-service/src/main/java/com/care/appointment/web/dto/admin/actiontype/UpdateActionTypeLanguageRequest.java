package com.care.appointment.web.dto.admin.actiontype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateActionTypeLanguageRequest {

    @NotNull
    private UUID actionTypeLanguageId;

    @NotNull
    private UUID actionTypeId;

    @NotBlank
    @Size(max = 10)
    private String languageCode;

    @NotBlank
    @Size(max = 100)
    private String name;

    private Boolean isActive = Boolean.TRUE;

    private Boolean isDeleted = Boolean.FALSE;
}



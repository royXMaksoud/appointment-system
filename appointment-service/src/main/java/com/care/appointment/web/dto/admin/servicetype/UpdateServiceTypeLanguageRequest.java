package com.care.appointment.web.dto.admin.servicetype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateServiceTypeLanguageRequest {

    @NotNull
    private UUID serviceTypeLanguageId;

    @NotNull
    private UUID serviceTypeId;

    @NotBlank
    @Size(max = 10)
    private String languageCode;

    @NotBlank
    @Size(max = 200)
    private String name;

    @Size(max = 1000)
    private String description;

    private Boolean isActive = Boolean.TRUE;

    private Boolean isDeleted = Boolean.FALSE;
}



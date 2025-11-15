package com.care.appointment.web.dto.admin.appointment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class NearestServiceCenterRequest {

    @NotNull
    private UUID beneficiaryId;

    @NotNull
    private UUID serviceTypeId;

    private Double latitude;
    private Double longitude;
    private Integer limit;
    private Integer searchWindowDays;
}




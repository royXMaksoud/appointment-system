package com.care.appointment.web.dto.admin.appointmentstatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAppointmentStatusRequest {

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 120)
    private String name;

    private Boolean isActive = Boolean.TRUE;

    private Boolean isDeleted = Boolean.FALSE;
}



package com.care.appointment.web.dto.admin.holiday;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHolidayRequest {

    @NotNull(message = "Organization branch ID is required")
    private UUID organizationBranchId;

    @NotNull(message = "Holiday date is required")
    private LocalDate holidayDate;

    @NotBlank(message = "Holiday name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;

    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    private String reason;

    private Boolean isRecurringYearly;

    private Boolean isActive;
}


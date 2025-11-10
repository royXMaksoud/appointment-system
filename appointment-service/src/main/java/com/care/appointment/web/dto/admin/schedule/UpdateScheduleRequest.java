package com.care.appointment.web.dto.admin.schedule;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduleRequest {

    @NotNull(message = "Organization branch ID is required")
    private UUID organizationBranchId;

    @NotNull(message = "Day of week is required")
    @Min(value = 0, message = "Day of week must be between 0 (Sunday) and 6 (Saturday)")
    @Max(value = 6, message = "Day of week must be between 0 (Sunday) and 6 (Saturday)")
    private Integer dayOfWeek;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotNull(message = "Slot duration is required")
    @Min(value = 5, message = "Slot duration must be at least 5 minutes")
    @Max(value = 240, message = "Slot duration must not exceed 240 minutes")
    private Integer slotDurationMinutes;

    @Min(value = 1, message = "Max capacity must be at least 1")
    private Integer maxCapacityPerSlot;

    private Boolean isActive;
}

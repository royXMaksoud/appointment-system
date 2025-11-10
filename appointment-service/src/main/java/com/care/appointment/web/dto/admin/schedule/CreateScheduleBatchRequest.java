package com.care.appointment.web.dto.admin.schedule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateScheduleBatchRequest {

    @NotNull(message = "Organization branch ID is required")
    private UUID organizationBranchId;

    @NotNull(message = "Days of week are required")
    @NotEmpty(message = "At least one day must be selected")
    @Size(min = 1, max = 7, message = "Select between 1 and 7 days")
    private List<@Min(value = 0, message = "Day must be between 0 (Sunday) and 6 (Saturday)") 
                @Max(value = 6, message = "Day must be between 0 (Sunday) and 6 (Saturday)") Integer> daysOfWeek;

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
    
    private UUID createdById;
}


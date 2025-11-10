package com.care.appointment.web.dto.admin.schedule;

import lombok.*;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {

    private UUID scheduleId;
    private UUID organizationBranchId;
    private Integer dayOfWeek;
    private String dayName; // Computed field (e.g., "Sunday", "Monday")
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotDurationMinutes;
    private Integer maxCapacityPerSlot;
    private Boolean isActive;
    private Boolean isDeleted;
    private UUID createdById;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer rowVersion;
}


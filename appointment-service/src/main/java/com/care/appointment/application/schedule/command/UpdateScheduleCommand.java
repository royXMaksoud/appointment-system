package com.care.appointment.application.schedule.command;

import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduleCommand {
    private UUID scheduleId;
    private UUID organizationBranchId;
    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotDurationMinutes;
    private Integer maxCapacityPerSlot;
    private Boolean isActive;
}


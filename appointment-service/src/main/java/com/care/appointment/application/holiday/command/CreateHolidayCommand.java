package com.care.appointment.application.holiday.command;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHolidayCommand {
    private UUID organizationBranchId;
    private LocalDate holidayDate;
    private String name;
    private String reason;
    private Boolean isRecurringYearly;
    private Boolean isActive;
    private UUID createdById;
}


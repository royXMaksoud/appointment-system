package com.care.appointment.domain.model;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Holiday {
    private UUID holidayId;
    private UUID organizationBranchId;
    private LocalDate holidayDate;
    private String name;
    private String reason;
    private Boolean isRecurringYearly;
    private Boolean isActive;
    private Boolean isDeleted;
    private UUID createdById;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer rowVersion;
}

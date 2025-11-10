package com.care.appointment.web.dto.admin.actiontype;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionTypeResponse {
    
    private UUID actionTypeId;
    private String name;
    private String code;
    private String description;
    private Boolean isActive;
    private Boolean isDeleted;
    private Boolean requiresTransfer;
    private Boolean completesAppointment;
    private String color;
    private Integer displayOrder;
    private UUID createdById;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer rowVersion;
}


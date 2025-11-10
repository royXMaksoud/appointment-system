package com.care.appointment.web.dto.admin.servicetype;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTypeResponse {
    
    private UUID serviceTypeId;
    private String name;
    private String description;
    private UUID parentId;
    private Boolean isActive;
    private Boolean isDeleted;
    private Boolean isLeaf;
    private String code;
    private Integer displayOrder;
    private UUID createdById;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer rowVersion;
}


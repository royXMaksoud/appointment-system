package com.care.appointment.web.dto.admin.servicetype;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ServiceTypeTreeNodeDTO {
    private UUID serviceTypeId;
    private UUID parentId;
    private String name;
    private String code;
    private Boolean leaf;
    private Integer displayOrder;
    private List<ServiceTypeTreeNodeDTO> children;
}




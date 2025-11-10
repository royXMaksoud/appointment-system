package com.care.appointment.web.dto.admin.servicetype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateServiceTypeRequest {
    
    @NotBlank(message = "{serviceType.name.notBlank}")
    @Size(max = 200, message = "{serviceType.name.size}")
    private String name;
    
    @Size(max = 1000, message = "{serviceType.description.size}")
    private String description;
    
    private UUID parentId;
    
    private Boolean isActive;
    
    private Boolean isLeaf;
    
    private String code;
    
    private Integer displayOrder;
}


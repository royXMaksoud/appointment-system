package com.care.appointment.web.dto.admin.servicetype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateServiceTypeRequest {
    
    @NotBlank(message = "{serviceType.name.notBlank}")
    @Size(max = 200, message = "{serviceType.name.size}")
    private String name;
    
    @Size(max = 1000, message = "{serviceType.description.size}")
    private String description;
    
    private UUID parentId;
    
    @Builder.Default
    private Boolean isActive = true;
    
    @Builder.Default
    private Boolean isLeaf = true;
    
    private String code;
    
    private Integer displayOrder;
}


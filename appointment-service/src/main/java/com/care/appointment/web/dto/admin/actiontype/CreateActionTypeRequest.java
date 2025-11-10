package com.care.appointment.web.dto.admin.actiontype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateActionTypeRequest {
    
    @NotBlank(message = "{actionType.name.notBlank}")
    @Size(max = 200, message = "{actionType.name.size}")
    private String name;
    
    @NotBlank(message = "{actionType.code.notBlank}")
    @Size(max = 50, message = "{actionType.code.size}")
    private String code;
    
    @Size(max = 500, message = "{actionType.description.size}")
    private String description;
    
    @Builder.Default
    private Boolean isActive = true;
    
    @Builder.Default
    private Boolean requiresTransfer = false;
    
    @Builder.Default
    private Boolean completesAppointment = false;
    
    private String color;
    
    private Integer displayOrder;
}


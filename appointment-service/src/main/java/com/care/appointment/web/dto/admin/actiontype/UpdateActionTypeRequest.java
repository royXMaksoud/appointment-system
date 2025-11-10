package com.care.appointment.web.dto.admin.actiontype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateActionTypeRequest {
    
    @NotBlank(message = "{actionType.name.notBlank}")
    @Size(max = 200, message = "{actionType.name.size}")
    private String name;
    
    @Size(max = 500, message = "{actionType.description.size}")
    private String description;
    
    private Boolean isActive;
    
    private Boolean requiresTransfer;
    
    private Boolean completesAppointment;
    
    private String color;
    
    private Integer displayOrder;
}


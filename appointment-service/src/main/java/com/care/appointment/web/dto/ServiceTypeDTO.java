package com.care.appointment.web.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class ServiceTypeDTO {
    
    private UUID serviceTypeId;
    
    private UUID parentServiceTypeId;
    
    private String code;
    
    private String name;  // localized name
    
    private String description;  // localized description
    
    private Boolean isActive;
    
    private List<ServiceTypeDTO> subServices;  // children services
}


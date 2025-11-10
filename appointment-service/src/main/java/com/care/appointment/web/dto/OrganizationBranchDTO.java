package com.care.appointment.web.dto;

import lombok.*;

import java.util.UUID;

/**
 * DTO for organization branch from access-management-service
 */
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class OrganizationBranchDTO {
    
    private UUID organizationBranchId;
    
    private String code;
    
    private String name;
    
    private UUID organizationId;
    
    private UUID countryId;
    
    private UUID locationId;
    
    private UUID branchTypeId;
    
    private String address;
    
    private Double latitude;
    
    private Double longitude;
    
    private Boolean isHeadquarter;
    
    private Boolean isActive;
}


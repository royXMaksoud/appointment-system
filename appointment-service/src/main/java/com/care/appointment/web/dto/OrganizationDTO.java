package com.care.appointment.web.dto;

import lombok.*;

import java.util.UUID;

/**
 * DTO for organization from reference-data-service
 */
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class OrganizationDTO {

    private UUID organizationId;

    private String code;

    private String name;

    private String description;

    private Boolean isActive;
}

package com.care.appointment.web.dto.admin.branchservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchServiceTypeSummaryDTO {

    private UUID organizationBranchId;
    private String branchName;
    private UUID organizationId;
    private String organizationName;
    private Long assignedServiceCount;
    private BigDecimal totalCost;
}



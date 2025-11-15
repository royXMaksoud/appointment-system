package com.care.appointment.web.dto.admin.branchservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchServiceTypeDetailDTO {

    private UUID organizationBranchId;
    private String branchName;
    private UUID organizationId;
    private String organizationName;
    private List<BranchServiceTypeNodeDTO> serviceTree;
}



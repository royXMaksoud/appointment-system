package com.care.appointment.web.dto.admin.branchservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchServiceTypeNodeDTO {

    private UUID serviceTypeId;
    private String name;
    private String code;
    private UUID parentServiceTypeId;
    private boolean leaf;
    private boolean assigned;
    private BigDecimal cost;
    private Integer displayOrder;

    @Builder.Default
    private List<BranchServiceTypeNodeDTO> children = new ArrayList<>();
}



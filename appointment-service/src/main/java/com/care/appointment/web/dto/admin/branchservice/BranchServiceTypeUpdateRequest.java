package com.care.appointment.web.dto.admin.branchservice;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchServiceTypeUpdateRequest {

    @Builder.Default
    @Valid
    private List<BranchServiceTypeAssignmentDTO> assignments = new ArrayList<>();
}



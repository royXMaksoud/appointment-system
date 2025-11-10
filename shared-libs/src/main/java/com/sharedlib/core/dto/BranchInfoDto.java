package com.sharedlib.core.dto;

import java.util.UUID;

/**
 * DTO representing organization branch information.
 */
public record BranchInfoDto(
        UUID branchId,
        String branchName,
        String location,
        String contactNumber
) {}

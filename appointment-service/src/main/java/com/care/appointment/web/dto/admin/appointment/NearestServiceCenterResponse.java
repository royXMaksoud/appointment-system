package com.care.appointment.web.dto.admin.appointment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class NearestServiceCenterResponse {

    private UUID organizationBranchId;
    private UUID organizationId;
    private String organizationName;
    private String branchName;
    private String branchCode;
    private String address;
    private Double branchLatitude;
    private Double branchLongitude;
    private Double beneficiaryLatitude;
    private Double beneficiaryLongitude;
    private Double distanceKm;

    private UUID serviceTypeId;
    private String serviceTypeName;

    private LocalDate nextAvailableDate;
    private LocalTime nextAvailableTime;
    private Integer dailyCapacity;
    private Integer maxCapacityPerSlot;
    private Integer slotsPerDay;
    private Long bookedCount;
    private Integer remainingCapacity;
}




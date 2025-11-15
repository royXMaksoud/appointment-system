package com.care.appointment.web.mapper;

import com.care.appointment.application.appointment.query.NearestServiceCenterQuery;
import com.care.appointment.domain.model.NearestServiceCenterOption;
import com.care.appointment.web.dto.admin.appointment.NearestServiceCenterRequest;
import com.care.appointment.web.dto.admin.appointment.NearestServiceCenterResponse;
import org.springframework.stereotype.Component;

@Component
public class AppointmentSuggestionWebMapper {

    public NearestServiceCenterQuery toQuery(NearestServiceCenterRequest request) {
        return NearestServiceCenterQuery.builder()
                .beneficiaryId(request.getBeneficiaryId())
                .serviceTypeId(request.getServiceTypeId())
                .latitudeOverride(request.getLatitude())
                .longitudeOverride(request.getLongitude())
                .limit(request.getLimit())
                .searchWindowDays(request.getSearchWindowDays())
                .build();
    }

    public NearestServiceCenterResponse toResponse(NearestServiceCenterOption option) {
        return NearestServiceCenterResponse.builder()
                .organizationBranchId(option.getOrganizationBranchId())
                .organizationId(option.getOrganizationId())
                .organizationName(option.getOrganizationName())
                .branchName(option.getBranchName())
                .branchCode(option.getBranchCode())
                .address(option.getAddress())
                .branchLatitude(option.getBranchLatitude())
                .branchLongitude(option.getBranchLongitude())
                .beneficiaryLatitude(option.getBeneficiaryLatitude())
                .beneficiaryLongitude(option.getBeneficiaryLongitude())
                .distanceKm(option.getDistanceKm())
                .serviceTypeId(option.getServiceTypeId())
                .serviceTypeName(option.getServiceTypeName())
                .nextAvailableDate(option.getNextAvailableDate())
                .nextAvailableTime(option.getNextAvailableTime())
                .dailyCapacity(option.getDailyCapacity())
                .maxCapacityPerSlot(option.getMaxCapacityPerSlot())
                .slotsPerDay(option.getSlotsPerDay())
                .bookedCount(option.getBookedCount())
                .remainingCapacity(option.getRemainingCapacity())
                .build();
    }
}




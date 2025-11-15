package com.care.appointment.web.mapper;

import com.care.appointment.domain.model.DashboardMetrics;
import com.care.appointment.web.dto.dashboard.DashboardMetricsResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting dashboard domain models to REST API responses
 */
@Component
public class DashboardWebMapper {

    /**
     * Convert domain DashboardMetrics to response DTO
     */
    public DashboardMetricsResponse toResponse(DashboardMetrics metrics) {
        if (metrics == null) {
            return null;
        }

        return DashboardMetricsResponse.builder()
                .totalAppointments(metrics.getTotalAppointments())
                .completionRate(metrics.getCompletionRate())
                .noShowRate(metrics.getNoShowRate())
                .cancellationRate(metrics.getCancellationRate())
                .transferredRate(metrics.getTransferredRate())
                .appointmentsByStatus(metrics.getAppointmentsByStatus())
                .appointmentsByServiceType(metrics.getAppointmentsByServiceType())
                .appointmentsByPriority(metrics.getAppointmentsByPriority())
                .beneficiaryByGender(metrics.getBeneficiaryByGender())
                .beneficiaryByAgeGroup(metrics.getBeneficiaryByAgeGroup())
                .appointmentsTrend(mapTrendPoints(metrics.getAppointmentsTrend()))
                .centerMetrics(mapCenterMetrics(metrics.getCenterMetrics()))
                .dateFrom(metrics.getDateFrom())
                .dateTo(metrics.getDateTo())
                .period(metrics.getPeriod())
                .appliedFilters(mapAppliedFilters(metrics.getAppliedFilters()))
                .build();
    }

    /**
     * Convert trend points
     */
    private java.util.List<DashboardMetricsResponse.TrendPointResponse> mapTrendPoints(
            java.util.List<DashboardMetrics.TrendPoint> trendPoints) {
        if (trendPoints == null) {
            return null;
        }
        return trendPoints.stream()
                .map(this::toTrendPointResponse)
                .toList();
    }

    /**
     * Convert single trend point
     */
    private DashboardMetricsResponse.TrendPointResponse toTrendPointResponse(
            DashboardMetrics.TrendPoint trendPoint) {
        if (trendPoint == null) {
            return null;
        }
        return DashboardMetricsResponse.TrendPointResponse.builder()
                .date(trendPoint.getDate())
                .dateLabel(trendPoint.getDateLabel())
                .totalAppointments(trendPoint.getTotalAppointments())
                .completed(trendPoint.getCompleted())
                .cancelled(trendPoint.getCancelled())
                .noShow(trendPoint.getNoShow())
                .requested(trendPoint.getRequested())
                .confirmed(trendPoint.getConfirmed())
                .build();
    }

    /**
     * Convert center metrics
     */
    private java.util.List<DashboardMetricsResponse.CenterMetricResponse> mapCenterMetrics(
            java.util.List<DashboardMetrics.CenterMetric> centerMetrics) {
        if (centerMetrics == null) {
            return null;
        }
        return centerMetrics.stream()
                .map(this::toCenterMetricResponse)
                .toList();
    }

    /**
     * Convert single center metric
     */
    private DashboardMetricsResponse.CenterMetricResponse toCenterMetricResponse(
            DashboardMetrics.CenterMetric centerMetric) {
        if (centerMetric == null) {
            return null;
        }
        return DashboardMetricsResponse.CenterMetricResponse.builder()
                .centerId(centerMetric.getCenterId())
                .centerName(centerMetric.getCenterName())
                .governorate(centerMetric.getGovernorate())
                .latitude(centerMetric.getLatitude())
                .longitude(centerMetric.getLongitude())
                .totalAppointments(centerMetric.getTotalAppointments())
                .completionRate(centerMetric.getCompletionRate())
                .noShowRate(centerMetric.getNoShowRate())
                .cancellationRate(centerMetric.getCancellationRate())
                .completedCount(centerMetric.getCompletedCount())
                .cancelledCount(centerMetric.getCancelledCount())
                .noShowCount(centerMetric.getNoShowCount())
                .confirmedCount(centerMetric.getConfirmedCount())
                .requestedCount(centerMetric.getRequestedCount())
                .build();
    }

    /**
     * Convert applied filters
     */
    private DashboardMetricsResponse.AppliedFiltersResponse mapAppliedFilters(
            DashboardMetrics.AppliedFilters appliedFilters) {
        if (appliedFilters == null) {
            return null;
        }
        return DashboardMetricsResponse.AppliedFiltersResponse.builder()
                .serviceTypeIds(appliedFilters.getServiceTypeIds())
                .statuses(appliedFilters.getStatuses())
                .centerIds(appliedFilters.getCenterIds())
                .governorates(appliedFilters.getGovernorates())
                .priority(appliedFilters.getPriority())
                .beneficiaryStatus(appliedFilters.getBeneficiaryStatus())
                .build();
    }
}

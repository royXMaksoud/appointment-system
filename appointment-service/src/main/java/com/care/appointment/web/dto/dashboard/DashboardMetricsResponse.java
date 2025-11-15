package com.care.appointment.web.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST API Response DTO for dashboard metrics
 * Serializable response for frontend consumption
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricsResponse {

    // Summary KPIs
    @JsonProperty("totalAppointments")
    private long totalAppointments;

    @JsonProperty("completionRate")
    private double completionRate;

    @JsonProperty("noShowRate")
    private double noShowRate;

    @JsonProperty("cancellationRate")
    private double cancellationRate;

    @JsonProperty("transferredRate")
    private double transferredRate;

    // Status breakdown
    @JsonProperty("appointmentsByStatus")
    private Map<String, Long> appointmentsByStatus;

    // Service type breakdown
    @JsonProperty("appointmentsByServiceType")
    private Map<String, Long> appointmentsByServiceType;

    // Priority breakdown
    @JsonProperty("appointmentsByPriority")
    private Map<String, Long> appointmentsByPriority;

    // Demographics
    @JsonProperty("beneficiaryByGender")
    private Map<String, Long> beneficiaryByGender;

    @JsonProperty("beneficiaryByAgeGroup")
    private Map<String, Long> beneficiaryByAgeGroup;

    // Trends
    @JsonProperty("appointmentsTrend")
    private List<TrendPointResponse> appointmentsTrend;

    // Centers on map
    @JsonProperty("centerMetrics")
    private List<CenterMetricResponse> centerMetrics;

    // Metadata
    @JsonProperty("dateFrom")
    private LocalDate dateFrom;

    @JsonProperty("dateTo")
    private LocalDate dateTo;

    @JsonProperty("period")
    private String period;

    @JsonProperty("appliedFilters")
    private AppliedFiltersResponse appliedFilters;

    /**
     * Trend point for time-series data
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendPointResponse {
        @JsonProperty("date")
        private LocalDate date;

        @JsonProperty("dateLabel")
        private String dateLabel;

        @JsonProperty("totalAppointments")
        private long totalAppointments;

        @JsonProperty("completed")
        private long completed;

        @JsonProperty("cancelled")
        private long cancelled;

        @JsonProperty("noShow")
        private long noShow;

        @JsonProperty("requested")
        private long requested;

        @JsonProperty("confirmed")
        private long confirmed;
    }

    /**
     * Center metric for map visualization
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CenterMetricResponse {
        @JsonProperty("centerId")
        private String centerId;

        @JsonProperty("centerName")
        private String centerName;

        @JsonProperty("governorate")
        private String governorate;

        @JsonProperty("latitude")
        private Double latitude;

        @JsonProperty("longitude")
        private Double longitude;

        @JsonProperty("totalAppointments")
        private long totalAppointments;

        @JsonProperty("completionRate")
        private double completionRate;

        @JsonProperty("noShowRate")
        private double noShowRate;

        @JsonProperty("cancellationRate")
        private double cancellationRate;

        @JsonProperty("completedCount")
        private long completedCount;

        @JsonProperty("cancelledCount")
        private long cancelledCount;

        @JsonProperty("noShowCount")
        private long noShowCount;

        @JsonProperty("confirmedCount")
        private long confirmedCount;

        @JsonProperty("requestedCount")
        private long requestedCount;
    }

    /**
     * Applied filters response
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppliedFiltersResponse {
        @JsonProperty("serviceTypeIds")
        private List<String> serviceTypeIds;

        @JsonProperty("statuses")
        private List<String> statuses;

        @JsonProperty("centerIds")
        private List<String> centerIds;

        @JsonProperty("governorates")
        private List<String> governorates;

        @JsonProperty("priority")
        private String priority;

        @JsonProperty("beneficiaryStatus")
        private String beneficiaryStatus;
    }
}

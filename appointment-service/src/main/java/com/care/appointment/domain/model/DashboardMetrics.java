package com.care.appointment.domain.model;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Domain model for dashboard metrics and analytics
 * Represents aggregated appointment data for reporting and visualization
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetrics {

    // Summary metrics
    private long totalAppointments;
    private double completionRate;        // Percentage
    private double noShowRate;            // Percentage
    private double cancellationRate;      // Percentage
    private double transferredRate;       // Percentage

    // Status breakdown
    private Map<String, Long> appointmentsByStatus;  // e.g., COMPLETED -> 500

    // Service type breakdown
    private Map<String, Long> appointmentsByServiceType;

    // Priority breakdown
    private Map<String, Long> appointmentsByPriority;  // URGENT, NORMAL

    // Beneficiary demographics
    private Map<String, Long> beneficiaryByGender;     // Male, Female, Other
    private Map<String, Long> beneficiaryByAgeGroup;   // 0-5, 6-15, 16-25, etc.

    // Trend data
    private List<TrendPoint> appointmentsTrend;  // Over time (daily/weekly/monthly)

    // Center-level metrics
    private List<CenterMetric> centerMetrics;

    // Metadata
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String period;  // DAILY, WEEKLY, MONTHLY
    private AppliedFilters appliedFilters;

    /**
     * Trend point for time-series visualization
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendPoint {
        private LocalDate date;
        private String dateLabel;  // e.g., "Jan 15", "Week 10", etc.
        private long totalAppointments;
        private long completed;
        private long cancelled;
        private long noShow;
        private long requested;
        private long confirmed;
    }

    /**
     * Center-level metric summary
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CenterMetric {
        private String centerId;
        private String centerName;
        private String governorate;
        private Double latitude;
        private Double longitude;
        private long totalAppointments;
        private double completionRate;
        private double noShowRate;
        private double cancellationRate;
        private long completedCount;
        private long cancelledCount;
        private long noShowCount;
        private long confirmedCount;
        private long requestedCount;
    }

    /**
     * Applied filters metadata
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppliedFilters {
        private List<String> serviceTypeIds;
        private List<String> statuses;
        private List<String> centerIds;
        private List<String> governorates;
        private String priority;
        private String beneficiaryStatus;
    }
}

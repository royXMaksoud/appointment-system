package com.care.appointment.web.dto.reports;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * Statistical Report - Aggregated metrics and statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Schema(description = "Statistical report with aggregated metrics")
public class StatisticalReportData {

    @Schema(description = "Overall summary statistics")
    private SummaryStatistics summary;

    @Schema(description = "Breakdown by appointment status")
    private List<StatusBreakdown> byStatus;

    @Schema(description = "Breakdown by priority level")
    private List<PriorityBreakdown> byPriority;

    @Schema(description = "Breakdown by service type")
    private List<ServiceTypeBreakdown> byServiceType;

    /**
     * Summary Statistics DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    @Schema(description = "Summary statistics")
    public static class SummaryStatistics {
        @Schema(description = "Total appointments")
        private long totalAppointments;

        @Schema(description = "Completed appointments")
        private long completedAppointments;

        @Schema(description = "Pending appointments")
        private long pendingAppointments;

        @Schema(description = "Cancelled appointments")
        private long cancelledAppointments;

        @Schema(description = "No-show appointments")
        private long noShowAppointments;

        @Schema(description = "Transferred appointments")
        private long transferredAppointments;

        @Schema(description = "Completion rate percentage")
        private double completionRate;

        @Schema(description = "No-show rate percentage")
        private double noShowRate;

        @Schema(description = "Cancellation rate percentage")
        private double cancellationRate;
    }

    /**
     * Status Breakdown DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    @Schema(description = "Status breakdown")
    public static class StatusBreakdown {
        @Schema(description = "Status value")
        private String status;

        @Schema(description = "Count of appointments with this status")
        private long count;

        @Schema(description = "Percentage")
        private String percentage;
    }

    /**
     * Priority Breakdown DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    @Schema(description = "Priority breakdown")
    public static class PriorityBreakdown {
        @Schema(description = "Priority level")
        private String priority;

        @Schema(description = "Count")
        private long count;

        @Schema(description = "Percentage")
        private String percentage;

        @Schema(description = "Completed count")
        private long completed;

        @Schema(description = "Pending count")
        private long pending;
    }

    /**
     * Service Type Breakdown DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    @Schema(description = "Service type breakdown")
    public static class ServiceTypeBreakdown {
        @Schema(description = "Service type name")
        private String serviceType;

        @Schema(description = "Count")
        private long count;

        @Schema(description = "Percentage")
        private String percentage;
    }
}

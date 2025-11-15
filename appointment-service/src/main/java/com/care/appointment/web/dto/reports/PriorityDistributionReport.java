package com.care.appointment.web.dto.reports;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * Priority Distribution Report - Analysis of appointments by priority level
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Schema(description = "Priority distribution analysis")
public class PriorityDistributionReport {

    @Schema(description = "Priority distribution data")
    private List<PriorityDistribution> distribution;

    @Schema(description = "Timeline distribution")
    private List<TimelineDistribution> timeline;

    /**
     * Priority Distribution DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    @Schema(description = "Priority distribution metrics")
    public static class PriorityDistribution {
        @Schema(description = "Priority level (LOW, MEDIUM, HIGH, URGENT)")
        private String priority;

        @Schema(description = "Total count")
        private long count;

        @Schema(description = "Percentage")
        private double percentage;

        @Schema(description = "Completed count")
        private long completed;

        @Schema(description = "Pending count")
        private long pending;

        @Schema(description = "Cancelled count")
        private long cancelled;

        @Schema(description = "No-show count")
        private long noShow;

        @Schema(description = "Completion rate")
        private String completionRate;
    }

    /**
     * Timeline Distribution DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    @Schema(description = "Timeline distribution")
    public static class TimelineDistribution {
        @Schema(description = "Time period (week, month, etc)")
        private String period;

        @Schema(description = "Count for this period")
        private long count;

        @Schema(description = "Percentage")
        private String percentage;
    }
}

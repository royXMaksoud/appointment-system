package com.care.appointment.web.dto.reports;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Organization Performance Report - Metrics for each partner organization
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Schema(description = "Organization performance metrics")
public class OrganizationPerformanceReport {

    @Schema(description = "Organization ID")
    private String organizationId;

    @Schema(description = "Organization name")
    private String organizationName;

    @Schema(description = "Total appointments for this organization")
    private long totalAppointments;

    @Schema(description = "Completed appointments")
    private long completedAppointments;

    @Schema(description = "Pending appointments")
    private long pendingAppointments;

    @Schema(description = "Cancelled appointments")
    private long cancelledAppointments;

    @Schema(description = "No-show appointments")
    private long noShowAppointments;

    @Schema(description = "Completion rate percentage")
    private String completionRate;

    @Schema(description = "Number of partnered centers")
    private int partneredCenters;

    @Schema(description = "Number of unique beneficiaries served")
    private long beneficiariesServed;

    @Schema(description = "Average response time in hours")
    private String averageResponseTime;
}

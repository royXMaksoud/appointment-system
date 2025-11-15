package com.care.appointment.web.dto.reports;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Center Performance Report - Metrics for each center/branch
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Schema(description = "Center/branch performance metrics")
public class CenterPerformanceReport {

    @Schema(description = "Center/Branch ID")
    private String centerId;

    @Schema(description = "Center/Branch name")
    private String centerName;

    @Schema(description = "Total appointments for this center")
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

    @Schema(description = "Average waiting time in days")
    private String averageWaitingTime;

    @Schema(description = "Number of staff members")
    private int staffCount;

    @Schema(description = "Number of unique beneficiaries served")
    private long beneficiariesServed;

    @Schema(description = "Number of organizations partnered")
    private int partneredOrganizations;
}

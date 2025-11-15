package com.care.appointment.web.dto.reports;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Detailed Report Row - Complete appointment information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Schema(description = "Detailed appointment report row")
public class DetailedReportData {

    @Schema(description = "Appointment ID")
    private String appointmentId;

    @Schema(description = "Beneficiary/Client name")
    private String beneficiaryName;

    @Schema(description = "Beneficiary ID/Reference")
    private String beneficiaryId;

    @Schema(description = "Organization name")
    private String organizationName;

    @Schema(description = "Center/Branch name")
    private String centerName;

    @Schema(description = "Service type")
    private String serviceType;

    @Schema(description = "Appointment scheduled date and time")
    private LocalDateTime appointmentDateTime;

    @Schema(description = "Appointment status (PENDING, CONFIRMED, COMPLETED, CANCELLED, NO_SHOW)")
    private String status;

    @Schema(description = "Priority level (LOW, MEDIUM, HIGH, URGENT)")
    private String priority;

    @Schema(description = "Service provider name")
    private String providerName;

    @Schema(description = "Contact number")
    private String contactNumber;

    @Schema(description = "Location/Address")
    private String location;

    @Schema(description = "Notes/Remarks")
    private String notes;

    @Schema(description = "Created date")
    private LocalDateTime createdDate;

    @Schema(description = "Last modified date")
    private LocalDateTime modifiedDate;
}

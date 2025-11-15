package com.care.appointment.web.dto.reports;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for Excel Report Generation
 *
 * Supports multi-dimensional filtering for appointment reports:
 * - Date range filtering
 * - Organization and center filtering
 * - Status filtering
 * - Priority filtering
 * - Service type filtering
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Schema(description = "Request for generating Excel reports")
public class ExcelReportRequest {

    @Schema(description = "Report type: DETAILED, STATISTICAL, CENTER, ORGANIZATION, PRIORITY")
    @NotNull(message = "Report type is required")
    private String reportType;

    @Schema(description = "Start date for report period")
    private LocalDate dateFrom;

    @Schema(description = "End date for report period")
    private LocalDate dateTo;

    @JsonProperty("organizationIds")
    @Schema(description = "Filter by organization IDs")
    private List<String> organizationIds;

    @JsonProperty("centerIds")
    @Schema(description = "Filter by center/branch IDs")
    private List<String> centerIds;

    @Schema(description = "Filter by appointment statuses")
    private List<String> statuses;

    @Schema(description = "Filter by priority levels (LOW, MEDIUM, HIGH, URGENT)")
    private List<String> priorities;

    @JsonProperty("serviceTypeIds")
    @Schema(description = "Filter by service type IDs")
    private List<String> serviceTypeIds;

    @Schema(description = "Language for report: ar, en")
    @Builder.Default
    private String language = "ar";

    /**
     * Validate the date range
     */
    public boolean isValidDateRange() {
        if (dateFrom == null || dateTo == null) {
            return true;
        }
        return !dateFrom.isAfter(dateTo);
    }
}

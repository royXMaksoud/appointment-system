package com.care.appointment.web.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for dashboard metrics filter
 * Contains all filter criteria for aggregating appointment data
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardFilterRequest {

    @JsonProperty("dateFrom")
    private LocalDate dateFrom;

    @JsonProperty("dateTo")
    private LocalDate dateTo;

    @JsonProperty("serviceTypeIds")
    private List<UUID> serviceTypeIds;  // UUID list

    @JsonProperty("statuses")
    private List<String> statuses;  // COMPLETED, CANCELLED, NO_SHOW, etc.

    @JsonProperty("centerIds")
    private List<UUID> centerIds;  // UUID list

    @JsonProperty("governorates")
    private List<String> governorates;  // Region/governorate codes

    @JsonProperty("priority")
    private String priority;  // URGENT or NORMAL

    @JsonProperty("beneficiaryStatus")
    private Boolean beneficiaryStatus;  // true = ACTIVE, false = INACTIVE

    @JsonProperty("period")
    private String period;  // DAILY, WEEKLY, MONTHLY (default: DAILY)

    /**
     * Validates date range
     */
    public boolean isValidDateRange() {
        if (dateFrom == null || dateTo == null) {
            return false;
        }
        return !dateFrom.isAfter(dateTo);
    }
}

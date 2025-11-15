package com.care.appointment.web.controller.admin;

import com.care.appointment.application.dashboard.DashboardQueryService;
import com.care.appointment.domain.model.DashboardMetrics;
import com.care.appointment.web.dto.dashboard.DashboardFilterRequest;
import com.care.appointment.web.dto.dashboard.DashboardMetricsResponse;
import com.care.appointment.web.mapper.DashboardWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST Controller for General Appointment Dashboard
 *
 * Provides comprehensive analytics and reporting endpoints for appointment system.
 * Supports multi-dimensional filtering, aggregation, and visualization data.
 *
 * Features:
 * - Dashboard metrics with KPIs
 * - Status distribution
 * - Service type breakdown
 * - Demographic analysis (gender, age groups)
 * - Time-series trends
 * - Geographic center metrics with map data
 * - Advanced filtering (date range, status, service, location, priority)
 *
 * All operations are read-only and optimized for performance with caching.
 */
@RestController
@RequestMapping({"/api/admin/appointments/dashboard", "/api/admin/Appointments/dashboard"})
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Appointment Dashboard", description = "Analytics and reporting APIs for appointment system")
public class GeneralDashboardController {

    private final DashboardQueryService dashboardQueryService;
    private final DashboardWebMapper dashboardMapper;

    /**
     * Get comprehensive dashboard metrics with optional filtering
     *
     * @param filter Optional filter criteria (date range, status, service type, center, priority)
     * @return Dashboard metrics including KPIs, charts data, and trend analysis
     */
    @PostMapping(
            value = "/metrics",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Get dashboard metrics",
            description = "Retrieves comprehensive dashboard metrics with appointment analytics"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Metrics calculated successfully",
            content = @Content(schema = @Schema(implementation = DashboardMetricsResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid filter parameters")
    @ApiResponse(responseCode = "500", description = "Server error calculating metrics")
    public ResponseEntity<DashboardMetricsResponse> getDashboardMetrics(
            @Valid @RequestBody(required = false) DashboardFilterRequest filter) {

        log.info("Fetching dashboard metrics with filter: {}", filter);

        // Set default date range if not provided (last 30 days)
        if (filter == null) {
            filter = DashboardFilterRequest.builder()
                    .dateFrom(LocalDate.now().minusMonths(1))
                    .dateTo(LocalDate.now())
                    .period("DAILY")
                    .build();
        }

        // Validate date range
        if (!filter.isValidDateRange()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            DashboardMetrics metrics = dashboardQueryService.getDashboardMetrics(filter);
            DashboardMetricsResponse response = dashboardMapper.toResponse(metrics);
            log.info("Successfully generated dashboard metrics for period: {} to {}",
                    filter.getDateFrom(), filter.getDateTo());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid dashboard filter: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error generating dashboard metrics", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get dashboard metrics with quick preset date filters
     *
     * @param preset Preset period: TODAY, THIS_WEEK, THIS_MONTH, THIS_YEAR, LAST_30_DAYS, LAST_90_DAYS
     * @return Dashboard metrics for the preset period
     */
    @PostMapping(
            value = "/metrics/preset/{preset}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Get dashboard metrics with preset period",
            description = "Retrieves dashboard metrics using a quick preset date range"
    )
    @ApiResponse(responseCode = "200", description = "Metrics calculated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid preset")
    public ResponseEntity<DashboardMetricsResponse> getDashboardMetricsPreset(
            @PathVariable String preset,
            @Valid @RequestBody(required = false) DashboardFilterRequest additionalFilters) {

        log.info("Fetching dashboard metrics with preset: {}", preset);

        DashboardFilterRequest filter = buildPresetFilter(preset);
        if (filter == null) {
            return ResponseEntity.badRequest().build();
        }

        // Merge additional filters if provided
        if (additionalFilters != null) {
            if (additionalFilters.getServiceTypeIds() != null) {
                filter.setServiceTypeIds(additionalFilters.getServiceTypeIds());
            }
            if (additionalFilters.getStatuses() != null) {
                filter.setStatuses(additionalFilters.getStatuses());
            }
            if (additionalFilters.getCenterIds() != null) {
                filter.setCenterIds(additionalFilters.getCenterIds());
            }
            if (additionalFilters.getGovernorates() != null) {
                filter.setGovernorates(additionalFilters.getGovernorates());
            }
            if (additionalFilters.getPriority() != null) {
                filter.setPriority(additionalFilters.getPriority());
            }
            if (additionalFilters.getBeneficiaryStatus() != null) {
                filter.setBeneficiaryStatus(additionalFilters.getBeneficiaryStatus());
            }
        }

        try {
            DashboardMetrics metrics = dashboardQueryService.getDashboardMetrics(filter);
            DashboardMetricsResponse response = dashboardMapper.toResponse(metrics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating preset dashboard metrics", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get summary KPI cards data (for quick view)
     *
     * @param filter Optional filter criteria
     * @return Quick summary with totals and rates
     */
    @PostMapping(
            value = "/kpis",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Get KPI summary cards",
            description = "Retrieves quick KPI summary for dashboard cards"
    )
    @ApiResponse(responseCode = "200", description = "KPIs calculated successfully")
    public ResponseEntity<KPISummary> getKPISummary(
            @Valid @RequestBody(required = false) DashboardFilterRequest filter) {

        if (filter == null) {
            filter = DashboardFilterRequest.builder()
                    .dateFrom(LocalDate.now().minusMonths(1))
                    .dateTo(LocalDate.now())
                    .period("DAILY")
                    .build();
        }

        try {
            DashboardMetrics metrics = dashboardQueryService.getDashboardMetrics(filter);
            KPISummary summary = KPISummary.builder()
                    .totalAppointments(metrics.getTotalAppointments())
                    .completionRate(metrics.getCompletionRate())
                    .noShowRate(metrics.getNoShowRate())
                    .cancellationRate(metrics.getCancellationRate())
                    .transferredRate(metrics.getTransferredRate())
                    .build();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Error generating KPI summary", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Build filter from preset name
     */
    private DashboardFilterRequest buildPresetFilter(String preset) {
        LocalDate today = LocalDate.now();

        return switch (preset.toUpperCase()) {
            case "TODAY" -> DashboardFilterRequest.builder()
                    .dateFrom(today)
                    .dateTo(today)
                    .period("DAILY")
                    .build();

            case "THIS_WEEK" -> DashboardFilterRequest.builder()
                    .dateFrom(today.minusDays(today.getDayOfWeek().getValue() - 1))
                    .dateTo(today)
                    .period("DAILY")
                    .build();

            case "THIS_MONTH" -> DashboardFilterRequest.builder()
                    .dateFrom(today.withDayOfMonth(1))
                    .dateTo(today)
                    .period("DAILY")
                    .build();

            case "THIS_YEAR" -> DashboardFilterRequest.builder()
                    .dateFrom(today.withDayOfYear(1))
                    .dateTo(today)
                    .period("MONTHLY")
                    .build();

            case "LAST_30_DAYS" -> DashboardFilterRequest.builder()
                    .dateFrom(today.minusDays(30))
                    .dateTo(today)
                    .period("DAILY")
                    .build();

            case "LAST_90_DAYS" -> DashboardFilterRequest.builder()
                    .dateFrom(today.minusDays(90))
                    .dateTo(today)
                    .period("WEEKLY")
                    .build();

            default -> null;
        };
    }

    /**
     * Quick KPI Summary DTO
     */
    @lombok.Getter
    @lombok.Setter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class KPISummary {
        private long totalAppointments;
        private double completionRate;
        private double noShowRate;
        private double cancellationRate;
        private double transferredRate;
    }
}

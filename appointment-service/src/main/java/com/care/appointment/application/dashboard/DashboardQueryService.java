package com.care.appointment.application.dashboard;

import com.care.appointment.domain.model.DashboardMetrics;
import com.care.appointment.infrastructure.db.repositories.AppointmentRepository;
import com.care.appointment.web.dto.dashboard.DashboardFilterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for querying and aggregating dashboard metrics
 * Handles complex appointment analytics and reporting
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DashboardQueryService {

    private final AppointmentRepository appointmentRepository;

    /**
     * Build comprehensive dashboard metrics based on filter criteria
     */
    public DashboardMetrics getDashboardMetrics(DashboardFilterRequest filter) {
        log.info("Building dashboard metrics with filters: {}", filter);

        // Validate filter
        if (filter == null) {
            filter = DashboardFilterRequest.builder()
                    .dateFrom(LocalDate.now().minusMonths(1))
                    .dateTo(LocalDate.now())
                    .period("DAILY")
                    .build();
        }

        if (!filter.isValidDateRange()) {
            throw new IllegalArgumentException("Invalid date range: dateFrom must be before or equal to dateTo");
        }

        // Default period if not specified
        if (filter.getPeriod() == null || filter.getPeriod().isEmpty()) {
            filter.setPeriod("DAILY");
        }

        // Get filtered appointments (this will use repository with custom queries)
        List<UUID> serviceTypeIds = emptyToNull(filter.getServiceTypeIds());
        List<String> statuses = emptyToNull(filter.getStatuses());
        List<UUID> centerIds = emptyToNull(filter.getCenterIds());

        boolean serviceTypeFilterDisabled = serviceTypeIds == null;
        boolean statusFilterDisabled = statuses == null;
        boolean centerFilterDisabled = centerIds == null;

        List<UUID> serviceTypeIdsParam = serviceTypeFilterDisabled ? List.of(DUMMY_UUID) : serviceTypeIds;
        List<String> statusesParam = statusFilterDisabled ? List.of(DUMMY_STATUS) : statuses;
        List<UUID> centerIdsParam = centerFilterDisabled ? List.of(DUMMY_UUID) : centerIds;

        List<?> rawResults = appointmentRepository.findAppointmentsForDashboard(
                filter.getDateFrom(),
                filter.getDateTo(),
                serviceTypeFilterDisabled,
                serviceTypeIdsParam,
                statusFilterDisabled,
                statusesParam,
                centerFilterDisabled,
                centerIdsParam,
                filter.getPriority(),
                filter.getBeneficiaryStatus()
        );

        // Convert raw results to AppointmentView objects
        List<AppointmentViewImpl> appointments = convertResultsToViews(rawResults);

        // Calculate summary metrics
        long totalAppointments = appointments.size();
        long completed = appointments.stream().filter(a -> "COMPLETED".equals(a.getStatus())).count();
        long cancelled = appointments.stream().filter(a -> "CANCELLED".equals(a.getStatus())).count();
        long noShow = appointments.stream().filter(a -> "NO_SHOW".equals(a.getStatus())).count();
        long transferred = appointments.stream().filter(a -> "TRANSFERRED".equals(a.getStatus())).count();

        double completionRate = totalAppointments > 0 ? (double) completed / totalAppointments * 100 : 0;
        double noShowRate = totalAppointments > 0 ? (double) noShow / totalAppointments * 100 : 0;
        double cancellationRate = totalAppointments > 0 ? (double) cancelled / totalAppointments * 100 : 0;
        double transferredRate = totalAppointments > 0 ? (double) transferred / totalAppointments * 100 : 0;

        // Status breakdown
        Map<String, Long> appointmentsByStatus = appointments.stream()
                .collect(Collectors.groupingBy(
                        AppointmentViewImpl::getStatus,
                        Collectors.counting()
                ));

        // Service type breakdown
        Map<String, Long> appointmentsByServiceType = appointments.stream()
                .collect(Collectors.groupingBy(
                        AppointmentViewImpl::getServiceTypeName,
                        Collectors.counting()
                ));

        // Priority breakdown
        Map<String, Long> appointmentsByPriority = appointments.stream()
                .collect(Collectors.groupingBy(
                        AppointmentViewImpl::getPriority,
                        Collectors.counting()
                ));

        // Beneficiary demographics
        Map<String, Long> beneficiaryByGender = appointments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getGender() != null ? a.getGender() : "Unknown",
                        Collectors.counting()
                ));

        Map<String, Long> beneficiaryByAgeGroup = calculateAgeDistribution(appointments);

        // Trend data
        List<DashboardMetrics.TrendPoint> trendPoints = calculateTrend(appointments, filter.getPeriod(),
                filter.getDateFrom(), filter.getDateTo());

        // Center metrics
        List<DashboardMetrics.CenterMetric> centerMetrics = calculateCenterMetrics(appointments, filter.getGovernorates());

        // Build the response
        return DashboardMetrics.builder()
                .totalAppointments(totalAppointments)
                .completionRate(Math.round(completionRate * 100.0) / 100.0)
                .noShowRate(Math.round(noShowRate * 100.0) / 100.0)
                .cancellationRate(Math.round(cancellationRate * 100.0) / 100.0)
                .transferredRate(Math.round(transferredRate * 100.0) / 100.0)
                .appointmentsByStatus(appointmentsByStatus)
                .appointmentsByServiceType(appointmentsByServiceType)
                .appointmentsByPriority(appointmentsByPriority)
                .beneficiaryByGender(beneficiaryByGender)
                .beneficiaryByAgeGroup(beneficiaryByAgeGroup)
                .appointmentsTrend(trendPoints)
                .centerMetrics(centerMetrics)
                .dateFrom(filter.getDateFrom())
                .dateTo(filter.getDateTo())
                .period(filter.getPeriod())
                .appliedFilters(DashboardMetrics.AppliedFilters.builder()
                        .serviceTypeIds(convertUuidListToStrings(serviceTypeIds))
                        .statuses(statuses)
                        .centerIds(convertUuidListToStrings(centerIds))
                        .governorates(filter.getGovernorates())
                        .priority(filter.getPriority())
                        .beneficiaryStatus(mapBeneficiaryStatus(filter.getBeneficiaryStatus()))
                        .build())
                .build();
    }

    /**
     * Convert raw SQL results to AppointmentViewImpl objects
     */
    private List<AppointmentViewImpl> convertResultsToViews(List<?> rawResults) {
        return rawResults.stream()
                .map(this::convertToView)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Convert single raw result (Object array) to AppointmentViewImpl
     */
    private AppointmentViewImpl convertToView(Object result) {
        try {
            if (result instanceof Object[]) {
                Object[] row = (Object[]) result;
                return AppointmentViewImpl.builder()
                        .id(toString(row[0]))
                        .centerId(toString(row[1]))
                        .centerName((String) row[2])
                        .governorate((String) row[3])
                        .latitude(toDouble(row[4]))
                        .longitude(toDouble(row[5]))
                        .appointmentDate(toLocalDate(row[6]))
                        .status((String) row[7])
                        .serviceTypeName((String) row[8])
                        .priority((String) row[9])
                        .gender((String) row[10])
                        .dateOfBirth(toLocalDate(row[11]))
                        .build();
            }
        } catch (Exception e) {
            log.warn("Error converting appointment result row: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Calculate age distribution based on date of birth
     */
    private Map<String, Long> calculateAgeDistribution(List<AppointmentViewImpl> appointments) {
        LocalDate today = LocalDate.now();

        return appointments.stream()
                .collect(Collectors.groupingBy(a -> {
                    if (a.getDateOfBirth() == null) return "Unknown";

                    int age = today.getYear() - a.getDateOfBirth().getYear();
                    if (today.getMonth().getValue() < a.getDateOfBirth().getMonth().getValue()) {
                        age--;
                    }

                    if (age < 6) return "0-5";
                    if (age < 16) return "6-15";
                    if (age < 26) return "16-25";
                    if (age < 36) return "26-35";
                    if (age < 46) return "36-45";
                    return "46+";
                }, Collectors.counting()));
    }

    /**
     * Calculate trend points based on period
     */
    private List<DashboardMetrics.TrendPoint> calculateTrend(List<AppointmentViewImpl> appointments,
                                                             String period, LocalDate dateFrom, LocalDate dateTo) {
        Map<Object, List<AppointmentViewImpl>> groupedByPeriod = new HashMap<>();

        if ("DAILY".equals(period)) {
            groupedByPeriod = appointments.stream()
                    .collect(Collectors.groupingBy(AppointmentViewImpl::getAppointmentDate));
        } else if ("WEEKLY".equals(period)) {
            groupedByPeriod = appointments.stream()
                    .collect(Collectors.groupingBy(a -> a.getAppointmentDate()
                            .get(WeekFields.ISO.weekOfWeekBasedYear())));
        } else if ("MONTHLY".equals(period)) {
            groupedByPeriod = appointments.stream()
                    .collect(Collectors.groupingBy(a -> YearMonth.from(a.getAppointmentDate())));
        }

        List<DashboardMetrics.TrendPoint> trendPoints = new ArrayList<>();

        if ("DAILY".equals(period)) {
            LocalDate current = dateFrom;
            while (!current.isAfter(dateTo)) {
                List<AppointmentViewImpl> dayAppointments = (List<AppointmentViewImpl>) groupedByPeriod.getOrDefault(current, List.of());
                trendPoints.add(buildTrendPoint(dayAppointments, current.toString(), current));
                current = current.plusDays(1);
            }
        } else if ("WEEKLY".equals(period)) {
            groupedByPeriod.forEach((week, appts) -> {
                trendPoints.add(buildTrendPoint(appts, "Week " + week, null));
            });
        } else if ("MONTHLY".equals(period)) {
            groupedByPeriod.forEach((month, appts) -> {
                trendPoints.add(buildTrendPoint(appts, month.toString(), null));
            });
        }

        return trendPoints.stream()
                .sorted(Comparator.comparing(p -> p.getDate() != null ? p.getDate() : LocalDate.now()))
                .collect(Collectors.toList());
    }

    /**
     * Build single trend point
     */
    private DashboardMetrics.TrendPoint buildTrendPoint(List<AppointmentViewImpl> appts, String label, LocalDate date) {
        long completed = appts.stream().filter(a -> "COMPLETED".equals(a.getStatus())).count();
        long cancelled = appts.stream().filter(a -> "CANCELLED".equals(a.getStatus())).count();
        long noShow = appts.stream().filter(a -> "NO_SHOW".equals(a.getStatus())).count();
        long requested = appts.stream().filter(a -> "REQUESTED".equals(a.getStatus())).count();
        long confirmed = appts.stream().filter(a -> "CONFIRMED".equals(a.getStatus())).count();

        return DashboardMetrics.TrendPoint.builder()
                .date(date)
                .dateLabel(label)
                .totalAppointments(appts.size())
                .completed(completed)
                .cancelled(cancelled)
                .noShow(noShow)
                .requested(requested)
                .confirmed(confirmed)
                .build();
    }

    /**
     * Calculate center-level metrics
     */
    private List<DashboardMetrics.CenterMetric> calculateCenterMetrics(List<AppointmentViewImpl> appointments,
                                                                       List<String> governorates) {
        Map<String, List<AppointmentViewImpl>> appointmentsByCenter = appointments.stream()
                .collect(Collectors.groupingBy(AppointmentViewImpl::getCenterId));

        return appointmentsByCenter.entrySet().stream()
                .map(entry -> {
                    String centerId = entry.getKey();
                    List<AppointmentViewImpl> centerAppts = entry.getValue();

                    long completed = centerAppts.stream().filter(a -> "COMPLETED".equals(a.getStatus())).count();
                    long cancelled = centerAppts.stream().filter(a -> "CANCELLED".equals(a.getStatus())).count();
                    long noShow = centerAppts.stream().filter(a -> "NO_SHOW".equals(a.getStatus())).count();
                    long confirmed = centerAppts.stream().filter(a -> "CONFIRMED".equals(a.getStatus())).count();
                    long requested = centerAppts.stream().filter(a -> "REQUESTED".equals(a.getStatus())).count();

                    long totalAppts = centerAppts.size();
                    double completionRate = totalAppts > 0 ? (double) completed / totalAppts * 100 : 0;
                    double noShowRate = totalAppts > 0 ? (double) noShow / totalAppts * 100 : 0;
                    double cancellationRate = totalAppts > 0 ? (double) cancelled / totalAppts * 100 : 0;

                    // Get first appointment's center details
                    AppointmentViewImpl sample = centerAppts.get(0);

                    return DashboardMetrics.CenterMetric.builder()
                            .centerId(centerId)
                            .centerName(sample.getCenterName())
                            .governorate(sample.getGovernorate())
                            .latitude(sample.getLatitude())
                            .longitude(sample.getLongitude())
                            .totalAppointments(totalAppts)
                            .completionRate(Math.round(completionRate * 100.0) / 100.0)
                            .noShowRate(Math.round(noShowRate * 100.0) / 100.0)
                            .cancellationRate(Math.round(cancellationRate * 100.0) / 100.0)
                            .completedCount(completed)
                            .cancelledCount(cancelled)
                            .noShowCount(noShow)
                            .confirmedCount(confirmed)
                            .requestedCount(requested)
                            .build();
                })
                .filter(metric -> governorates == null || governorates.isEmpty() ||
                        governorates.contains(metric.getGovernorate()))
                .collect(Collectors.toList());
    }

    /**
     * Simple view model implementation for appointment data used in aggregations
     */
    @lombok.Getter
    @lombok.Setter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AppointmentViewImpl {
        private String id;
        private String centerId;
        private String centerName;
        private String governorate;
        private Double latitude;
        private Double longitude;
        private LocalDate appointmentDate;
        private String status;
        private String serviceTypeName;
        private String priority;
        private String gender;
        private LocalDate dateOfBirth;
    }

    private List<String> convertUuidListToStrings(List<UUID> uuids) {
        if (uuids == null) {
            return null;
        }
        return uuids.stream()
                .map(UUID::toString)
                .toList();
    }

    private static final UUID DUMMY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String DUMMY_STATUS = "__DASHBOARD_NO_STATUS__";

    private <T> List<T> emptyToNull(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list;
    }

    private String mapBeneficiaryStatus(Boolean beneficiaryStatus) {
        if (beneficiaryStatus == null) {
            return null;
        }
        return beneficiaryStatus ? "ACTIVE" : "INACTIVE";
    }

    private String toString(Object value) {
        return value != null ? value.toString() : null;
    }

    private Double toDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return null;
    }

    private LocalDate toLocalDate(Object value) {
        if (value instanceof LocalDate localDate) {
            return localDate;
        }
        if (value instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return null;
    }
}

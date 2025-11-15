package com.care.appointment.web.controller.admin;

import com.care.appointment.application.reports.ExcelReportService;
import com.care.appointment.web.dto.reports.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

/**
 * REST Controller for Excel Report Generation
 *
 * Provides endpoints for generating various types of Excel reports
 * with advanced filtering capabilities.
 *
 * Reports Available:
 * - DETAILED: Full appointment details with all information
 * - STATISTICAL: Aggregated metrics and statistics
 * - CENTER: Center/branch performance analysis
 * - ORGANIZATION: Organization performance analysis
 * - PRIORITY: Priority distribution analysis
 *
 * Features:
 * - Multi-language support (Arabic, English)
 * - Advanced filtering (date range, organization, center, status, priority)
 * - Direct Excel file download
 * - Report preview data
 * - Applied filters documentation
 */
@RestController
@RequestMapping({"/api/admin/appointments/reports", "/api/admin/Appointments/reports"})
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Excel Reports", description = "Excel report generation and export APIs")
public class ExcelReportsController {

    private final ExcelReportService excelReportService;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Generate detailed appointment report
     *
     * @param request Report request with filters
     * @return Excel file with detailed appointment data
     */
    @PostMapping(
            value = "/detailed",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    @Operation(
            summary = "Generate detailed appointment report",
            description = "Generates an Excel file with detailed information for each appointment"
    )
    @ApiResponse(responseCode = "200", description = "Report generated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    @ApiResponse(responseCode = "500", description = "Server error generating report")
    public ResponseEntity<byte[]> generateDetailedReport(@Valid @RequestBody ExcelReportRequest request) {
        log.info("Generating detailed report with filters: {}", request);

        try {
            // Mock data for detailed report
            List<DetailedReportData> data = generateMockDetailedData(request);

            // Generate Excel file
            byte[] excelContent = excelReportService.generateDetailedReport(data, request);

            return buildExcelResponse(excelContent, "detailed", request.getLanguage());
        } catch (Exception e) {
            log.error("Error generating detailed report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Generate statistical report
     *
     * @param request Report request with filters
     * @return Excel file with aggregated statistics
     */
    @PostMapping(
            value = "/statistical",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    @Operation(
            summary = "Generate statistical report",
            description = "Generates an Excel file with aggregated metrics and statistics"
    )
    @ApiResponse(responseCode = "200", description = "Report generated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    public ResponseEntity<byte[]> generateStatisticalReport(@Valid @RequestBody ExcelReportRequest request) {
        log.info("Generating statistical report with filters: {}", request);

        try {
            // Mock data for statistical report
            StatisticalReportData data = generateMockStatisticalData(request);

            // Generate Excel file
            byte[] excelContent = excelReportService.generateStatisticalReport(data, request);

            return buildExcelResponse(excelContent, "statistical", request.getLanguage());
        } catch (Exception e) {
            log.error("Error generating statistical report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Generate center performance report
     *
     * @param request Report request with filters
     * @return Excel file with center performance metrics
     */
    @PostMapping(
            value = "/center",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    @Operation(
            summary = "Generate center performance report",
            description = "Generates an Excel file with performance metrics for each center"
    )
    @ApiResponse(responseCode = "200", description = "Report generated successfully")
    public ResponseEntity<byte[]> generateCenterReport(@Valid @RequestBody ExcelReportRequest request) {
        log.info("Generating center performance report with filters: {}", request);

        try {
            // Mock data for center report
            List<CenterPerformanceReport> data = generateMockCenterData(request);

            // Generate Excel file
            byte[] excelContent = excelReportService.generateCenterReport(data, request);

            return buildExcelResponse(excelContent, "center", request.getLanguage());
        } catch (Exception e) {
            log.error("Error generating center report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Generate organization performance report
     *
     * @param request Report request with filters
     * @return Excel file with organization performance metrics
     */
    @PostMapping(
            value = "/organization",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    @Operation(
            summary = "Generate organization performance report",
            description = "Generates an Excel file with performance metrics for each organization"
    )
    @ApiResponse(responseCode = "200", description = "Report generated successfully")
    public ResponseEntity<byte[]> generateOrganizationReport(@Valid @RequestBody ExcelReportRequest request) {
        log.info("Generating organization performance report with filters: {}", request);

        try {
            // Mock data for organization report
            List<OrganizationPerformanceReport> data = generateMockOrganizationData(request);

            // Generate Excel file
            byte[] excelContent = excelReportService.generateOrganizationReport(data, request);

            return buildExcelResponse(excelContent, "organization", request.getLanguage());
        } catch (Exception e) {
            log.error("Error generating organization report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Generate priority distribution report
     *
     * @param request Report request with filters
     * @return Excel file with priority distribution analysis
     */
    @PostMapping(
            value = "/priority",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    @Operation(
            summary = "Generate priority distribution report",
            description = "Generates an Excel file with priority distribution analysis"
    )
    @ApiResponse(responseCode = "200", description = "Report generated successfully")
    public ResponseEntity<byte[]> generatePriorityReport(@Valid @RequestBody ExcelReportRequest request) {
        log.info("Generating priority distribution report with filters: {}", request);

        try {
            // Mock data for priority report
            PriorityDistributionReport data = generateMockPriorityData(request);

            // Generate Excel file
            byte[] excelContent = excelReportService.generatePriorityReport(data, request);

            return buildExcelResponse(excelContent, "priority", request.getLanguage());
        } catch (Exception e) {
            log.error("Error generating priority report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper methods for mock data generation

    private List<DetailedReportData> generateMockDetailedData(ExcelReportRequest request) {
        List<DetailedReportData> data = new ArrayList<>();

        data.add(DetailedReportData.builder()
                .appointmentId("APT-001")
                .beneficiaryName("أحمد محمد")
                .beneficiaryId("BEN-001")
                .organizationName("منظمة النور")
                .centerName("المركز الرئيسي")
                .serviceType("الصحة")
                .appointmentDateTime(LocalDateTime.now())
                .status("مؤكدة")
                .priority("عالية")
                .providerName("د. فاطمة")
                .contactNumber("+963 999 888 777")
                .location("دمشق")
                .notes("متابعة دورية")
                .createdDate(LocalDateTime.now().minusDays(5))
                .modifiedDate(LocalDateTime.now())
                .build());

        data.add(DetailedReportData.builder()
                .appointmentId("APT-002")
                .beneficiaryName("فاطمة علي")
                .beneficiaryId("BEN-002")
                .organizationName("منظمة الأمل")
                .centerName("مركز الشمال")
                .serviceType("التعليم")
                .appointmentDateTime(LocalDateTime.now().plusDays(3))
                .status("قيد الانتظار")
                .priority("متوسطة")
                .providerName("أ. خالد")
                .contactNumber("+963 988 777 666")
                .location("حلب")
                .notes("جلسة تدريبية")
                .createdDate(LocalDateTime.now().minusDays(2))
                .modifiedDate(LocalDateTime.now())
                .build());

        return data;
    }

    private StatisticalReportData generateMockStatisticalData(ExcelReportRequest request) {
        StatisticalReportData.SummaryStatistics summary = StatisticalReportData.SummaryStatistics.builder()
                .totalAppointments(156)
                .completedAppointments(98)
                .pendingAppointments(35)
                .cancelledAppointments(15)
                .noShowAppointments(8)
                .transferredAppointments(5)
                .completionRate(62.82)
                .noShowRate(5.13)
                .cancellationRate(9.62)
                .build();

        List<StatisticalReportData.StatusBreakdown> byStatus = new ArrayList<>();
        byStatus.add(StatisticalReportData.StatusBreakdown.builder()
                .status("مؤكدة").count(98).percentage("62.82%").build());
        byStatus.add(StatisticalReportData.StatusBreakdown.builder()
                .status("قيد الانتظار").count(35).percentage("22.44%").build());
        byStatus.add(StatisticalReportData.StatusBreakdown.builder()
                .status("ملغاة").count(15).percentage("9.62%").build());
        byStatus.add(StatisticalReportData.StatusBreakdown.builder()
                .status("لم يحضر").count(8).percentage("5.13%").build());

        List<StatisticalReportData.PriorityBreakdown> byPriority = new ArrayList<>();
        byPriority.add(StatisticalReportData.PriorityBreakdown.builder()
                .priority("عالية").count(78).percentage("50%").completed(62).pending(12).build());
        byPriority.add(StatisticalReportData.PriorityBreakdown.builder()
                .priority("متوسطة").count(55).percentage("35.26%").completed(32).pending(18).build());
        byPriority.add(StatisticalReportData.PriorityBreakdown.builder()
                .priority("طارئة").count(18).percentage("11.54%").completed(18).pending(0).build());
        byPriority.add(StatisticalReportData.PriorityBreakdown.builder()
                .priority("منخفضة").count(5).percentage("3.21%").completed(2).pending(3).build());

        List<StatisticalReportData.ServiceTypeBreakdown> byServiceType = new ArrayList<>();
        byServiceType.add(StatisticalReportData.ServiceTypeBreakdown.builder()
                .serviceType("صحة").count(89).percentage("57.05%").build());
        byServiceType.add(StatisticalReportData.ServiceTypeBreakdown.builder()
                .serviceType("تعليم").count(45).percentage("28.85%").build());
        byServiceType.add(StatisticalReportData.ServiceTypeBreakdown.builder()
                .serviceType("خدمات اجتماعية").count(22).percentage("14.10%").build());

        return StatisticalReportData.builder()
                .summary(summary)
                .byStatus(byStatus)
                .byPriority(byPriority)
                .byServiceType(byServiceType)
                .build();
    }

    private List<CenterPerformanceReport> generateMockCenterData(ExcelReportRequest request) {
        List<CenterPerformanceReport> data = new ArrayList<>();

        data.add(CenterPerformanceReport.builder()
                .centerId("CENTER-001")
                .centerName("المركز الرئيسي")
                .totalAppointments(89)
                .completedAppointments(67)
                .pendingAppointments(15)
                .cancelledAppointments(5)
                .noShowAppointments(2)
                .completionRate("75.28%")
                .averageWaitingTime("2.5 أيام")
                .staffCount(12)
                .beneficiariesServed(89)
                .partneredOrganizations(3)
                .build());

        data.add(CenterPerformanceReport.builder()
                .centerId("CENTER-002")
                .centerName("مركز الشمال")
                .totalAppointments(45)
                .completedAppointments(28)
                .pendingAppointments(12)
                .cancelledAppointments(4)
                .noShowAppointments(1)
                .completionRate("62.22%")
                .averageWaitingTime("3.2 أيام")
                .staffCount(7)
                .beneficiariesServed(45)
                .partneredOrganizations(2)
                .build());

        data.add(CenterPerformanceReport.builder()
                .centerId("CENTER-003")
                .centerName("مركز الجنوب")
                .totalAppointments(22)
                .completedAppointments(15)
                .pendingAppointments(5)
                .cancelledAppointments(2)
                .noShowAppointments(0)
                .completionRate("68.18%")
                .averageWaitingTime("2.1 أيام")
                .staffCount(5)
                .beneficiariesServed(22)
                .partneredOrganizations(1)
                .build());

        return data;
    }

    private List<OrganizationPerformanceReport> generateMockOrganizationData(ExcelReportRequest request) {
        List<OrganizationPerformanceReport> data = new ArrayList<>();

        data.add(OrganizationPerformanceReport.builder()
                .organizationId("ORG-001")
                .organizationName("منظمة النور")
                .totalAppointments(67)
                .completedAppointments(52)
                .pendingAppointments(10)
                .cancelledAppointments(3)
                .noShowAppointments(2)
                .completionRate("77.61%")
                .partneredCenters(3)
                .beneficiariesServed(67)
                .averageResponseTime("2 ساعات")
                .build());

        data.add(OrganizationPerformanceReport.builder()
                .organizationId("ORG-002")
                .organizationName("منظمة الأمل")
                .totalAppointments(55)
                .completedAppointments(35)
                .pendingAppointments(15)
                .cancelledAppointments(4)
                .noShowAppointments(1)
                .completionRate("63.64%")
                .partneredCenters(2)
                .beneficiariesServed(55)
                .averageResponseTime("3 ساعات")
                .build());

        data.add(OrganizationPerformanceReport.builder()
                .organizationId("ORG-003")
                .organizationName("منظمة الرحمة")
                .totalAppointments(34)
                .completedAppointments(23)
                .pendingAppointments(8)
                .cancelledAppointments(2)
                .noShowAppointments(1)
                .completionRate("67.65%")
                .partneredCenters(2)
                .beneficiariesServed(34)
                .averageResponseTime("2.5 ساعات")
                .build());

        return data;
    }

    private PriorityDistributionReport generateMockPriorityData(ExcelReportRequest request) {
        List<PriorityDistributionReport.PriorityDistribution> distribution = new ArrayList<>();

        distribution.add(PriorityDistributionReport.PriorityDistribution.builder()
                .priority("عالية")
                .count(78)
                .percentage(50.0)
                .completed(62)
                .pending(12)
                .cancelled(3)
                .noShow(1)
                .completionRate("79.49%")
                .build());

        distribution.add(PriorityDistributionReport.PriorityDistribution.builder()
                .priority("متوسطة")
                .count(55)
                .percentage(35.26)
                .completed(32)
                .pending(18)
                .cancelled(4)
                .noShow(1)
                .completionRate("58.18%")
                .build());

        distribution.add(PriorityDistributionReport.PriorityDistribution.builder()
                .priority("طارئة")
                .count(18)
                .percentage(11.54)
                .completed(18)
                .pending(0)
                .cancelled(0)
                .noShow(0)
                .completionRate("100%")
                .build());

        distribution.add(PriorityDistributionReport.PriorityDistribution.builder()
                .priority("منخفضة")
                .count(5)
                .percentage(3.21)
                .completed(2)
                .pending(3)
                .cancelled(0)
                .noShow(0)
                .completionRate("40%")
                .build());

        List<PriorityDistributionReport.TimelineDistribution> timeline = new ArrayList<>();
        timeline.add(PriorityDistributionReport.TimelineDistribution.builder()
                .period("الأسبوع 1").count(35).percentage("22.44%").build());
        timeline.add(PriorityDistributionReport.TimelineDistribution.builder()
                .period("الأسبوع 2").count(42).percentage("26.92%").build());
        timeline.add(PriorityDistributionReport.TimelineDistribution.builder()
                .period("الأسبوع 3").count(48).percentage("30.77%").build());
        timeline.add(PriorityDistributionReport.TimelineDistribution.builder()
                .period("الأسبوع 4").count(31).percentage("19.87%").build());

        return PriorityDistributionReport.builder()
                .distribution(distribution)
                .timeline(timeline)
                .build();
    }

    private ResponseEntity<byte[]> buildExcelResponse(byte[] content, String reportType, String language) {
        String fileName = buildFileName(reportType, language);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(content.length);

        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    private String buildFileName(String reportType, String language) {
        String timestamp = LocalDateTime.now().format(DATETIME_FORMATTER).replace(":", "-").replace(" ", "_");
        String reportName = switch (reportType.toLowerCase()) {
            case "detailed" -> language.equals("ar") ? "تقرير_مفصل" : "detailed_report";
            case "statistical" -> language.equals("ar") ? "تقرير_احصائي" : "statistical_report";
            case "center" -> language.equals("ar") ? "تقرير_المراكز" : "center_report";
            case "organization" -> language.equals("ar") ? "تقرير_المنظمات" : "organization_report";
            case "priority" -> language.equals("ar") ? "توزيع_الأولويات" : "priority_distribution";
            default -> "report";
        };

        return reportName + "_" + timestamp + ".xlsx";
    }
}

package com.care.appointment.application.reports;

import com.care.appointment.web.dto.reports.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service for generating Excel reports
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelReportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public byte[] generateDetailedReport(List<DetailedReportData> data, ExcelReportRequest request) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(
                request.getLanguage().equals("ar") ? "تفاصيل الإحالات" : "Appointment Details"
            );

            CellStyle headerStyle = createHeaderStyle(workbook);
            String[] headers = getDetailedReportHeaders(request.getLanguage());
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (DetailedReportData detail : data) {
                Row row = sheet.createRow(rowNum++);
                fillDetailedReportRow(row, detail);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            addFiltersSheet(workbook, request);
            return workbookToBytes(workbook);
        } catch (IOException e) {
            log.error("Error generating detailed report", e);
            throw new RuntimeException("Failed to generate report", e);
        }
    }

    public byte[] generateStatisticalReport(StatisticalReportData data, ExcelReportRequest request) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            boolean isArabic = request.getLanguage().equals("ar");

            addSummarySheet(workbook, data.getSummary(), isArabic);
            addStatusSheet(workbook, data.getByStatus(), isArabic);
            addPrioritySheet(workbook, data.getByPriority(), isArabic);
            addServiceTypeSheet(workbook, data.getByServiceType(), isArabic);
            addFiltersSheet(workbook, request);

            return workbookToBytes(workbook);
        } catch (IOException e) {
            log.error("Error generating statistical report", e);
            throw new RuntimeException("Failed to generate report", e);
        }
    }

    public byte[] generateCenterReport(List<CenterPerformanceReport> data, ExcelReportRequest request) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(
                request.getLanguage().equals("ar") ? "أداء المراكز" : "Center Performance"
            );

            CellStyle headerStyle = createHeaderStyle(workbook);
            String[] headers = getCenterReportHeaders(request.getLanguage());
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (CenterPerformanceReport center : data) {
                Row row = sheet.createRow(rowNum++);
                fillCenterReportRow(row, center);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            addFiltersSheet(workbook, request);
            return workbookToBytes(workbook);
        } catch (IOException e) {
            log.error("Error generating center report", e);
            throw new RuntimeException("Failed to generate report", e);
        }
    }

    public byte[] generateOrganizationReport(List<OrganizationPerformanceReport> data, ExcelReportRequest request) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(
                request.getLanguage().equals("ar") ? "أداء المنظمات" : "Organization Performance"
            );

            CellStyle headerStyle = createHeaderStyle(workbook);
            String[] headers = getOrganizationReportHeaders(request.getLanguage());
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (OrganizationPerformanceReport org : data) {
                Row row = sheet.createRow(rowNum++);
                fillOrganizationReportRow(row, org);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            addFiltersSheet(workbook, request);
            return workbookToBytes(workbook);
        } catch (IOException e) {
            log.error("Error generating organization report", e);
            throw new RuntimeException("Failed to generate report", e);
        }
    }

    public byte[] generatePriorityReport(PriorityDistributionReport data, ExcelReportRequest request) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            boolean isArabic = request.getLanguage().equals("ar");

            addPriorityDistributionSheet(workbook, data.getDistribution(), isArabic);
            addTimelineSheet(workbook, data.getTimeline(), isArabic);
            addFiltersSheet(workbook, request);

            return workbookToBytes(workbook);
        } catch (IOException e) {
            log.error("Error generating priority report", e);
            throw new RuntimeException("Failed to generate report", e);
        }
    }

    private void addSummarySheet(Workbook workbook, StatisticalReportData.SummaryStatistics summary, boolean isArabic) {
        Sheet sheet = workbook.createSheet(isArabic ? "الملخص" : "Summary");
        CellStyle headerStyle = createHeaderStyle(workbook);

        String[] labels = isArabic ? new String[]{"الفئة", "القيمة"} : new String[]{"Category", "Value"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < labels.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(labels[i]);
            cell.setCellStyle(headerStyle);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put(isArabic ? "إجمالي الإحالات" : "Total Appointments", summary.getTotalAppointments());
        data.put(isArabic ? "المكتملة" : "Completed", summary.getCompletedAppointments());
        data.put(isArabic ? "قيد الانتظار" : "Pending", summary.getPendingAppointments());
        data.put(isArabic ? "الملغاة" : "Cancelled", summary.getCancelledAppointments());
        data.put(isArabic ? "لم يحضر" : "No Show", summary.getNoShowAppointments());

        int rowNum = 1;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            Object value = entry.getValue();
            if (value instanceof Number) {
                row.createCell(1).setCellValue(((Number) value).doubleValue());
            } else {
                row.createCell(1).setCellValue(value.toString());
            }
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void addStatusSheet(Workbook workbook, List<StatisticalReportData.StatusBreakdown> data, boolean isArabic) {
        Sheet sheet = workbook.createSheet(isArabic ? "الحالات" : "Status");
        CellStyle headerStyle = createHeaderStyle(workbook);

        String[] headers = isArabic ? new String[]{"الحالة", "العدد", "النسبة"} :
                                       new String[]{"Status", "Count", "Percentage"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (StatisticalReportData.StatusBreakdown status : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(status.getStatus());
            row.createCell(1).setCellValue(status.getCount());
            row.createCell(2).setCellValue(status.getPercentage());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void addPrioritySheet(Workbook workbook, List<StatisticalReportData.PriorityBreakdown> data, boolean isArabic) {
        Sheet sheet = workbook.createSheet(isArabic ? "الأولويات" : "Priorities");
        CellStyle headerStyle = createHeaderStyle(workbook);

        String[] headers = isArabic ?
            new String[]{"الأولوية", "العدد", "النسبة", "المكتملة", "قيد الانتظار"} :
            new String[]{"Priority", "Count", "Percentage", "Completed", "Pending"};

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (StatisticalReportData.PriorityBreakdown priority : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(priority.getPriority());
            row.createCell(1).setCellValue(priority.getCount());
            row.createCell(2).setCellValue(priority.getPercentage());
            row.createCell(3).setCellValue(priority.getCompleted());
            row.createCell(4).setCellValue(priority.getPending());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void addServiceTypeSheet(Workbook workbook, List<StatisticalReportData.ServiceTypeBreakdown> data, boolean isArabic) {
        Sheet sheet = workbook.createSheet(isArabic ? "نوع الخدمة" : "Service Type");
        CellStyle headerStyle = createHeaderStyle(workbook);

        String[] headers = isArabic ?
            new String[]{"نوع الخدمة", "العدد", "النسبة"} :
            new String[]{"Service Type", "Count", "Percentage"};

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (StatisticalReportData.ServiceTypeBreakdown service : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(service.getServiceType());
            row.createCell(1).setCellValue(service.getCount());
            row.createCell(2).setCellValue(service.getPercentage());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void addPriorityDistributionSheet(Workbook workbook, List<PriorityDistributionReport.PriorityDistribution> data, boolean isArabic) {
        Sheet sheet = workbook.createSheet(isArabic ? "توزيع الأولويات" : "Priority Distribution");
        CellStyle headerStyle = createHeaderStyle(workbook);

        String[] headers = isArabic ?
            new String[]{"الأولوية", "الإجمالي", "النسبة", "مكتملة", "قيد الانتظار", "ملغاة", "لم يحضر"} :
            new String[]{"Priority", "Total", "Percentage", "Completed", "Pending", "Cancelled", "No Show"};

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (PriorityDistributionReport.PriorityDistribution priority : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(priority.getPriority());
            row.createCell(1).setCellValue(priority.getCount());
            row.createCell(2).setCellValue(priority.getPercentage() + "%");
            row.createCell(3).setCellValue(priority.getCompleted());
            row.createCell(4).setCellValue(priority.getPending());
            row.createCell(5).setCellValue(priority.getCancelled());
            row.createCell(6).setCellValue(priority.getNoShow());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void addTimelineSheet(Workbook workbook, List<PriorityDistributionReport.TimelineDistribution> data, boolean isArabic) {
        Sheet sheet = workbook.createSheet(isArabic ? "التوزيع الزمني" : "Timeline");
        CellStyle headerStyle = createHeaderStyle(workbook);

        String[] headers = isArabic ?
            new String[]{"الفترة", "العدد", "النسبة"} :
            new String[]{"Period", "Count", "Percentage"};

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (PriorityDistributionReport.TimelineDistribution timeline : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(timeline.getPeriod());
            row.createCell(1).setCellValue(timeline.getCount());
            row.createCell(2).setCellValue(timeline.getPercentage());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void addFiltersSheet(Workbook workbook, ExcelReportRequest request) {
        Sheet sheet = workbook.createSheet("Filters");
        CellStyle headerStyle = createHeaderStyle(workbook);

        String[] headers = {"Filter", "Value"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        rowNum = addFilterRow(sheet, rowNum, "Report Type", request.getReportType());
        if (request.getDateFrom() != null) {
            rowNum = addFilterRow(sheet, rowNum, "Date From", request.getDateFrom().format(DATE_FORMATTER));
        }
        if (request.getDateTo() != null) {
            rowNum = addFilterRow(sheet, rowNum, "Date To", request.getDateTo().format(DATE_FORMATTER));
        }
        if (request.getOrganizationIds() != null && !request.getOrganizationIds().isEmpty()) {
            rowNum = addFilterRow(sheet, rowNum, "Organizations", String.join(", ", request.getOrganizationIds()));
        }
        if (request.getCenterIds() != null && !request.getCenterIds().isEmpty()) {
            rowNum = addFilterRow(sheet, rowNum, "Centers", String.join(", ", request.getCenterIds()));
        }
        if (request.getStatuses() != null && !request.getStatuses().isEmpty()) {
            rowNum = addFilterRow(sheet, rowNum, "Statuses", String.join(", ", request.getStatuses()));
        }
        if (request.getPriorities() != null && !request.getPriorities().isEmpty()) {
            rowNum = addFilterRow(sheet, rowNum, "Priorities", String.join(", ", request.getPriorities()));
        }
        addFilterRow(sheet, rowNum, "Generated At", LocalDateTime.now().format(DATETIME_FORMATTER));

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private int addFilterRow(Sheet sheet, int rowNum, String filter, String value) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(filter);
        row.createCell(1).setCellValue(value);
        return rowNum + 1;
    }

    private void fillDetailedReportRow(Row row, DetailedReportData data) {
        row.createCell(0).setCellValue(nvl(data.getAppointmentId()));
        row.createCell(1).setCellValue(nvl(data.getBeneficiaryName()));
        row.createCell(2).setCellValue(nvl(data.getOrganizationName()));
        row.createCell(3).setCellValue(nvl(data.getCenterName()));
        row.createCell(4).setCellValue(nvl(data.getServiceType()));
        row.createCell(5).setCellValue(data.getAppointmentDateTime() != null ?
            data.getAppointmentDateTime().format(DATETIME_FORMATTER) : "");
        row.createCell(6).setCellValue(nvl(data.getStatus()));
        row.createCell(7).setCellValue(nvl(data.getPriority()));
        row.createCell(8).setCellValue(nvl(data.getProviderName()));
        row.createCell(9).setCellValue(nvl(data.getContactNumber()));
        row.createCell(10).setCellValue(nvl(data.getNotes()));
    }

    private void fillCenterReportRow(Row row, CenterPerformanceReport data) {
        row.createCell(0).setCellValue(nvl(data.getCenterName()));
        row.createCell(1).setCellValue(data.getTotalAppointments());
        row.createCell(2).setCellValue(data.getCompletedAppointments());
        row.createCell(3).setCellValue(data.getPendingAppointments());
        row.createCell(4).setCellValue(data.getCancelledAppointments());
        row.createCell(5).setCellValue(data.getNoShowAppointments());
        row.createCell(6).setCellValue(nvl(data.getCompletionRate()));
        row.createCell(7).setCellValue(nvl(data.getAverageWaitingTime()));
        row.createCell(8).setCellValue(data.getStaffCount());
        row.createCell(9).setCellValue(data.getBeneficiariesServed());
    }

    private void fillOrganizationReportRow(Row row, OrganizationPerformanceReport data) {
        row.createCell(0).setCellValue(nvl(data.getOrganizationName()));
        row.createCell(1).setCellValue(data.getTotalAppointments());
        row.createCell(2).setCellValue(data.getCompletedAppointments());
        row.createCell(3).setCellValue(data.getPendingAppointments());
        row.createCell(4).setCellValue(data.getCancelledAppointments());
        row.createCell(5).setCellValue(data.getNoShowAppointments());
        row.createCell(6).setCellValue(nvl(data.getCompletionRate()));
        row.createCell(7).setCellValue(data.getPartneredCenters());
        row.createCell(8).setCellValue(data.getBeneficiariesServed());
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private String[] getDetailedReportHeaders(String language) {
        if (language.equals("ar")) {
            return new String[]{
                "رقم الإحالة", "المستفيد", "المنظمة", "المركز", "نوع الخدمة",
                "التاريخ والوقت", "الحالة", "الأولوية", "مزود الخدمة", "رقم الاتصال", "ملاحظات"
            };
        }
        return new String[]{
            "Appointment ID", "Beneficiary", "Organization", "Center", "Service Type",
            "Date/Time", "Status", "Priority", "Provider", "Contact", "Notes"
        };
    }

    private String[] getCenterReportHeaders(String language) {
        if (language.equals("ar")) {
            return new String[]{
                "المركز", "الإجمالي", "المكتملة", "قيد الانتظار", "الملغاة", "لم يحضر",
                "معدل الإنجاز", "متوسط الانتظار", "الموظفون", "المستفيدون"
            };
        }
        return new String[]{
            "Center", "Total", "Completed", "Pending", "Cancelled", "No Show",
            "Completion Rate", "Avg Wait Time", "Staff", "Beneficiaries"
        };
    }

    private String[] getOrganizationReportHeaders(String language) {
        if (language.equals("ar")) {
            return new String[]{
                "المنظمة", "الإجمالي", "المكتملة", "قيد الانتظار", "الملغاة", "لم يحضر",
                "معدل الإنجاز", "المراكز الشريكة", "المستفيدون"
            };
        }
        return new String[]{
            "Organization", "Total", "Completed", "Pending", "Cancelled", "No Show",
            "Completion Rate", "Partnered Centers", "Beneficiaries"
        };
    }

    private String nvl(String value) {
        return value != null ? value : "";
    }

    private byte[] workbookToBytes(Workbook workbook) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        return baos.toByteArray();
    }
}

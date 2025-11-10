package com.ftp.authservice.web.controller;

import com.ftp.authservice.application.user.command.CreateUserCommand;
import com.ftp.authservice.domain.model.User;
import com.ftp.authservice.domain.ports.in.user.SaveUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/users/bulk")
@RequiredArgsConstructor
public class UserBulkController {

    private final SaveUseCase saveUserUseCase;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${access-management-service.url:http://localhost:8080}")
    private String accessManagementServiceUrl;

    @GetMapping(value = "/template")
    @Operation(summary = "Download users bulk import template (Excel with dropdowns)")
    public ResponseEntity<byte[]> downloadBulkTemplate() {
        XSSFWorkbook workbook = null;
        ByteArrayOutputStream baos = null;
        try {
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Users");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Base headers (without role columns)
            List<String> headers = new ArrayList<>();
            headers.add("firstName");
            headers.add("fatherName");
            headers.add("surName");
            headers.add("emailAddress");
            headers.add("password");
            headers.add("authMethod");
            headers.add("tenantId");
            headers.add("organizationId");
            headers.add("organizationBranchId");
            headers.add("accountKind");
            headers.add("enabled");
            headers.add("validFrom");
            headers.add("validTo");
            headers.add("employmentStartDate");
            headers.add("employmentEndDate");
            headers.add("passwordExpiresAt");
            headers.add("mustChangePassword");
            headers.add("language");
            headers.add("profileImageUrl");
            
            // Fetch systems with roles from access-management-service
            List<Map<String, Object>> systemsWithRoles = fetchSystemsWithRoles();
            
            // Add system role columns dynamically
            Map<String, Integer> systemRoleColumnMap = new HashMap<>();
            for (Map<String, Object> system : systemsWithRoles) {
                String systemName = (String) system.get("systemName");
                String columnName = "role_" + systemName.replaceAll("[^a-zA-Z0-9]", "_");
                headers.add(columnName);
                systemRoleColumnMap.put(columnName, headers.size() - 1);
            }
            
            // Write headers
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4000);
            }
            
            // Create dropdown lists - use a separate sheet for dropdown values
            Sheet dropdownSheet = workbook.createSheet("_Dropdowns");
            dropdownSheet.protectSheet("");
            
            int dropdownRowNum = 0;
            
            // AuthMethod dropdown (column 5)
            dropdownSheet.createRow(dropdownRowNum++).createCell(0).setCellValue("LOCAL");
            dropdownSheet.createRow(dropdownRowNum++).createCell(0).setCellValue("OAUTH");
            dropdownSheet.createRow(dropdownRowNum++).createCell(0).setCellValue("FEDERATED_AD");
            createDropdown(sheet, workbook, 5, 1000, "_Dropdowns!A1:A3");
            
            // AccountKind dropdown (column 9)
            int accountKindStart = dropdownRowNum;
            dropdownSheet.createRow(dropdownRowNum++).createCell(0).setCellValue("GENERAL");
            dropdownSheet.createRow(dropdownRowNum++).createCell(0).setCellValue("OPERATOR");
            dropdownSheet.createRow(dropdownRowNum++).createCell(0).setCellValue("ADMIN");
            createDropdown(sheet, workbook, 9, 1000, "_Dropdowns!A" + (accountKindStart + 1) + ":A" + dropdownRowNum);
            
            // Enabled dropdown (column 10)
            int enabledStart = dropdownRowNum;
            dropdownSheet.createRow(dropdownRowNum++).createCell(0).setCellValue("true");
            dropdownSheet.createRow(dropdownRowNum++).createCell(0).setCellValue("false");
            createDropdown(sheet, workbook, 10, 1000, "_Dropdowns!A" + (enabledStart + 1) + ":A" + dropdownRowNum);
            
            // MustChangePassword dropdown (column 16)
            int mustChangeStart = dropdownRowNum;
            dropdownSheet.createRow(dropdownRowNum++).createCell(0).setCellValue("true");
            dropdownSheet.createRow(dropdownRowNum++).createCell(0).setCellValue("false");
            createDropdown(sheet, workbook, 16, 1000, "_Dropdowns!A" + (mustChangeStart + 1) + ":A" + dropdownRowNum);
            
            // Language dropdown (column 17)
            int languageStart = dropdownRowNum;
            dropdownSheet.createRow(dropdownRowNum++).createCell(0).setCellValue("en");
            dropdownSheet.createRow(dropdownRowNum++).createCell(0).setCellValue("ar");
            dropdownSheet.createRow(dropdownRowNum++).createCell(0).setCellValue("fr");
            createDropdown(sheet, workbook, 17, 1000, "_Dropdowns!A" + (languageStart + 1) + ":A" + dropdownRowNum);
            
            // TenantId dropdown (column 6) - using tenant names
            int tenantStart = dropdownRowNum;
            List<Map<String, Object>> tenants = fetchTenants();
            dropdownSheet.createRow(dropdownRowNum++).createCell(0).setCellValue(""); // Empty option
            for (Map<String, Object> tenant : tenants) {
                String tenantName = (String) tenant.get("name");
                String tenantId = tenant.get("id").toString();
                dropdownSheet.createRow(dropdownRowNum++).createCell(0).setCellValue(tenantName);
                // Store tenantId in column B for mapping
                dropdownSheet.getRow(dropdownRowNum - 1).createCell(1).setCellValue(tenantId);
            }
            createDropdown(sheet, workbook, 6, 1000, "_Dropdowns!A" + (tenantStart + 1) + ":A" + dropdownRowNum);
            
            // Create dropdowns for each system role column
            for (Map<String, Object> system : systemsWithRoles) {
                String systemId = system.get("systemId").toString();
                String systemName = (String) system.get("systemName");
                String columnName = "role_" + systemName.replaceAll("[^a-zA-Z0-9]", "_");
                Integer columnIndex = systemRoleColumnMap.get(columnName);
                
                if (columnIndex != null) {
                    // Fetch roles for this system
                    List<Map<String, Object>> roles = fetchSystemRoles(systemId);
                    if (!roles.isEmpty()) {
                        int roleStartRow = dropdownRowNum;
                        // Add empty option first
                        dropdownSheet.createRow(dropdownRowNum++).createCell(0).setCellValue("");
                        // Add roles in format "SystemName#RoleName"
                        for (Map<String, Object> role : roles) {
                            String roleName = (String) role.get("name");
                            String roleId = role.get("id").toString();
                            String displayValue = systemName + "#" + roleName;
                            dropdownSheet.createRow(dropdownRowNum++).createCell(0).setCellValue(displayValue);
                            // Store mapping for import
                            dropdownSheet.getRow(dropdownRowNum - 1).createCell(1).setCellValue(roleId);
                            dropdownSheet.getRow(dropdownRowNum - 1).createCell(2).setCellValue(systemId);
                        }
                        // Create dropdown for this system column
                        createDropdown(sheet, workbook, columnIndex, 1000, 
                            "_Dropdowns!A" + (roleStartRow + 1) + ":A" + dropdownRowNum);
                    }
                }
            }
            
            // Hide dropdown sheet
            workbook.setSheetHidden(workbook.getSheetIndex(dropdownSheet), true);
            
            // Add example row
            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue("John");
            exampleRow.createCell(3).setCellValue("john@example.com");
            exampleRow.createCell(5).setCellValue("LOCAL");
            exampleRow.createCell(9).setCellValue("GENERAL");
            exampleRow.createCell(10).setCellValue("true");
            exampleRow.createCell(17).setCellValue("en");
            
            // Write workbook to stream
            baos = new ByteArrayOutputStream();
            workbook.write(baos);
            byte[] bytes = baos.toByteArray();
            
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            responseHeaders.setContentDispositionFormData("attachment", "users-template.xlsx");
            responseHeaders.setContentLength(bytes.length);
            
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error creating template: " + e.getMessage()).getBytes());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                // Ignore
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchSystemsWithRoles() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                accessManagementServiceUrl + "/api/systems?page=0&size=500", 
                Map.class
            );
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("content")) {
                List<Map<String, Object>> systems = (List<Map<String, Object>>) body.get("content");
                // Filter systems that have roles
                List<Map<String, Object>> systemsWithRoles = new ArrayList<>();
                for (Map<String, Object> system : systems) {
                    String systemId = system.get("systemId") != null ? system.get("systemId").toString() : null;
                    if (systemId != null) {
                        List<Map<String, Object>> roles = fetchSystemRoles(systemId);
                        if (!roles.isEmpty()) {
                            Map<String, Object> systemInfo = new HashMap<>();
                            systemInfo.put("systemId", systemId);
                            systemInfo.put("systemName", system.get("name"));
                            systemsWithRoles.add(systemInfo);
                        }
                    }
                }
                return systemsWithRoles;
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch systems: " + e.getMessage());
        }
        return new ArrayList<>();
    }
    
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchSystemRoles(String systemId) {
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(
                accessManagementServiceUrl + "/api/system-roles/dropdown/by-system/" + systemId, 
                List.class
            );
            List<Map<String, Object>> roles = (List<Map<String, Object>>) response.getBody();
            return roles != null ? roles : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Failed to fetch roles for system " + systemId + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchTenants() {
        try {
            // Try /api/tenants/dropdown first, then fallback to /api/v1/tenants/dropdown
            ResponseEntity<List> response = null;
            try {
                response = restTemplate.getForEntity(
                    accessManagementServiceUrl + "/api/tenants/dropdown", 
                    List.class
                );
            } catch (Exception e) {
                response = restTemplate.getForEntity(
                    accessManagementServiceUrl + "/api/v1/tenants/dropdown", 
                    List.class
                );
            }
            List<Map<String, Object>> tenants = (List<Map<String, Object>>) response.getBody();
            return tenants != null ? tenants : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Failed to fetch tenants: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private String convertTenantNameToId(String tenantValue) {
        if (tenantValue == null || tenantValue.isBlank()) {
            return null;
        }
        
        // If it's already a UUID format, return as is
        try {
            UUID.fromString(tenantValue.trim());
            return tenantValue.trim();
        } catch (IllegalArgumentException e) {
            // Not a UUID, try to find by name
        }
        
        // Try to find tenant by name
        try {
            List<Map<String, Object>> tenants = fetchTenants();
            for (Map<String, Object> tenant : tenants) {
                String tenantName = (String) tenant.get("name");
                if (tenantValue.equalsIgnoreCase(tenantName)) {
                    return tenant.get("id").toString();
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to convert tenant name to ID: " + e.getMessage());
        }
        
        // If not found, return original value (might cause error later, but let validation handle it)
        return tenantValue;
    }
    
    private void createDropdown(Sheet sheet, Workbook workbook, int columnIndex, int maxRows, String formula) {
        try {
            DataValidationHelper helper = sheet.getDataValidationHelper();
            CellRangeAddressList addressList = new CellRangeAddressList(1, maxRows, columnIndex, columnIndex);
            DataValidationConstraint constraint = helper.createFormulaListConstraint(formula);
            DataValidation validation = helper.createValidation(constraint, addressList);
            validation.setShowErrorBox(true);
            validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            sheet.addValidationData(validation);
        } catch (Exception e) {
            // Ignore dropdown creation errors
        }
    }


    @PostMapping(value = "/import", consumes = "multipart/form-data")
    @Operation(summary = "Import users in bulk from Excel and assign roles")
    public ResponseEntity<Map<String, Object>> importUsers(@RequestPart("file") MultipartFile file) {
        int created = 0;
        int failed = 0;
        int rolesAssigned = 0;
        List<String> errors = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Read header row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Empty file"));
            }
            
            // Map column indices
            Map<String, Integer> columnMap = new HashMap<>();
            List<String> systemRoleColumns = new ArrayList<>();
            for (Cell cell : headerRow) {
                String header = getCellValueAsString(cell);
                columnMap.put(header, cell.getColumnIndex());
                // Identify system role columns (start with "role_")
                if (header != null && header.startsWith("role_")) {
                    systemRoleColumns.add(header);
                }
            }
            
            // Process data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    // Extract basic user values
                    String firstName = getCellValue(columnMap, row, "firstName");
                    String fatherName = getCellValue(columnMap, row, "fatherName");
                    String surName = getCellValue(columnMap, row, "surName");
                    String email = getCellValue(columnMap, row, "emailAddress");
                    String password = getCellValue(columnMap, row, "password");
                    String authMethod = getCellValueOrDefault(columnMap, row, "authMethod", "LOCAL");
                    // tenantId can be tenant name (from dropdown) or UUID - convert if needed
                    String tenantIdValue = getCellValue(columnMap, row, "tenantId");
                    String tenantId = convertTenantNameToId(tenantIdValue);
                    String organizationId = getCellValue(columnMap, row, "organizationId");
                    String organizationBranchId = getCellValue(columnMap, row, "organizationBranchId");
                    String accountKind = getCellValueOrDefault(columnMap, row, "accountKind", "GENERAL");
                    String enabled = getCellValueOrDefault(columnMap, row, "enabled", "true");
                    String validFrom = getCellValue(columnMap, row, "validFrom");
                    String validTo = getCellValue(columnMap, row, "validTo");
                    String employmentStartDate = getCellValue(columnMap, row, "employmentStartDate");
                    String employmentEndDate = getCellValue(columnMap, row, "employmentEndDate");
                    String passwordExpiresAt = getCellValue(columnMap, row, "passwordExpiresAt");
                    String mustChangePassword = getCellValueOrDefault(columnMap, row, "mustChangePassword", "false");
                    String language = getCellValueOrDefault(columnMap, row, "language", "en");
                    String profileImageUrl = getCellValue(columnMap, row, "profileImageUrl");
                    
                    // Validate required fields
                    if (firstName == null || firstName.isBlank() || email == null || email.isBlank()) {
                        failed++;
                        errors.add("Row " + (i + 1) + ": firstName and emailAddress are required");
                        continue;
                    }
                    
                    // Create user
                    CreateUserCommand cmd = CreateUserCommand.builder()
                            .firstName(firstName)
                            .fatherName(emptyToNull(fatherName))
                            .surName(emptyToNull(surName))
                            .fullName(null)
                            .email(email)
                            .isEmailVerified(null)
                            .password(emptyToNull(password))
                            .authMethod(authMethod)
                            .lastAuthProvider(null)
                            .passwordExpiresAt(toInstant(passwordExpiresAt))
                            .mustChangePassword(parseBoolean(mustChangePassword, false))
                            .tenantId(toUuid(tenantId))
                            .organizationId(toUuid(organizationId))
                            .organizationBranchId(toUuid(organizationBranchId))
                            .accountKind(accountKind)
                            .enabled(parseBoolean(enabled, true))
                            .deleted(false)
                            .validFrom(toInstant(validFrom))
                            .validTo(toInstant(validTo))
                            .mustRenewAt(null)
                            .employmentStartDate(toLocalDate(employmentStartDate))
                            .employmentEndDate(toLocalDate(employmentEndDate))
                            .language(language)
                            .profileImageUrl(emptyToNull(profileImageUrl))
                            .createdById(null)
                            .build();
                    
                    User saved = saveUserUseCase.saveUser(cmd);
                    if (saved != null && saved.getId() != null) {
                        created++;
                        
                        // Process system role columns - extract roles from dropdown values
                        for (String roleColumnName : systemRoleColumns) {
                            String roleValue = getCellValue(columnMap, row, roleColumnName);
                            if (roleValue != null && !roleValue.isBlank()) {
                                try {
                                    // Parse "SystemName#RoleName" format
                                    if (roleValue.contains("#")) {
                                        String[] parts = roleValue.split("#", 2);
                                        if (parts.length == 2) {
                                            String systemName = parts[0].trim();
                                            String roleName = parts[1].trim();
                                            
                                            // Find system by name and get role ID
                                            UUID roleId = findRoleIdBySystemAndRoleName(systemName, roleName);
                                            if (roleId != null) {
                                                assignRoleToUserByRoleId(saved.getId(), roleId, tenantId);
                                                rolesAssigned++;
                                            } else {
                                                errors.add("Row " + (i + 1) + ": Role not found: " + roleValue);
                                            }
                                        }
                                    } else {
                                        // Try to parse as direct roleId (UUID)
                                        try {
                                            UUID roleId = UUID.fromString(roleValue.trim());
                                            assignRoleToUserByRoleId(saved.getId(), roleId, tenantId);
                                            rolesAssigned++;
                                        } catch (IllegalArgumentException e) {
                                            errors.add("Row " + (i + 1) + ": Invalid role format: " + roleValue);
                                        }
                                    }
                                } catch (Exception e) {
                                    errors.add("Row " + (i + 1) + ": Failed to assign role from " + roleColumnName + ": " + e.getMessage());
                                }
                            }
                        }
                    } else {
                        failed++;
                        errors.add("Row " + (i + 1) + ": Failed to create user");
                    }
                } catch (Exception ex) {
                    failed++;
                    errors.add("Row " + (i + 1) + ": " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", "Failed to parse file: " + e.getMessage()));
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("created", created);
        result.put("failed", failed);
        result.put("rolesAssigned", rolesAssigned);
        if (!errors.isEmpty()) {
            result.put("errors", errors.subList(0, Math.min(10, errors.size()))); // Limit to 10 errors
        }
        return ResponseEntity.ok(result);
    }
    
    @SuppressWarnings("unchecked")
    private UUID findRoleIdBySystemAndRoleName(String systemName, String roleName) {
        try {
            // Fetch all systems
            ResponseEntity<Map> systemsResponse = restTemplate.getForEntity(
                accessManagementServiceUrl + "/api/systems?page=0&size=500", 
                Map.class
            );
            Map<String, Object> systemsBody = systemsResponse.getBody();
            if (systemsBody != null && systemsBody.containsKey("content")) {
                List<Map<String, Object>> systems = (List<Map<String, Object>>) systemsBody.get("content");
                
                // Find system by name
                for (Map<String, Object> system : systems) {
                    String sysName = (String) system.get("name");
                    if (systemName.equalsIgnoreCase(sysName)) {
                        String systemId = system.get("systemId").toString();
                        // Fetch roles for this system
                        List<Map<String, Object>> roles = fetchSystemRoles(systemId);
                        for (Map<String, Object> role : roles) {
                            String rName = (String) role.get("name");
                            if (roleName.equalsIgnoreCase(rName)) {
                                return UUID.fromString(role.get("id").toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to find role: " + e.getMessage());
        }
        return null;
    }
    
    private void assignRoleToUserByRoleId(UUID userId, UUID systemRoleId, String tenantId) {
        try {
            String url = accessManagementServiceUrl + "/api/user-system-roles/assign";
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("userId", userId.toString());
            requestBody.put("systemRoleId", systemRoleId.toString());
            if (tenantId != null && !tenantId.isBlank()) {
                requestBody.put("tenantId", tenantId);
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (tenantId != null && !tenantId.isBlank()) {
                headers.set("X-Tenant-Id", tenantId);
            }
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            restTemplate.postForEntity(url, request, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign role: " + e.getMessage(), e);
        }
    }

    private String getCellValue(Map<String, Integer> columnMap, Row row, String columnName) {
        Integer colIndex = columnMap.get(columnName);
        if (colIndex == null) return null;
        return getCellValueAsString(row.getCell(colIndex));
    }

    private String getCellValueOrDefault(Map<String, Integer> columnMap, Row row, String columnName, String defaultValue) {
        String value = getCellValue(columnMap, row, columnName);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toInstant().toString();
                } else {
                    double numValue = cell.getNumericCellValue();
                    if (numValue == (long) numValue) {
                        return String.valueOf((long) numValue);
                    } else {
                        return String.valueOf(numValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }


    // Helper methods
    private static String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private static UUID toUuid(String s) {
        try {
            return (s == null || s.isBlank()) ? null : UUID.fromString(s);
        } catch (Exception e) {
            return null;
        }
    }

    private static Boolean parseBoolean(String s, boolean def) {
        if (s == null || s.isBlank()) return def;
        return "true".equalsIgnoreCase(s) || "1".equals(s);
    }

    private static java.time.Instant toInstant(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            // Try ISO format first
            try {
                return java.time.Instant.parse(s);
            } catch (Exception e) {
                // Try parsing as date
                return java.time.LocalDateTime.parse(s).atZone(java.time.ZoneId.systemDefault()).toInstant();
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static java.time.LocalDate toLocalDate(String s) {
        try {
            return (s == null || s.isBlank()) ? null : java.time.LocalDate.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

}


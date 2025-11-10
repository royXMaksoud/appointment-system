package com.care.appointment.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Beneficiary document information")
public class BeneficiaryDocumentDTO {
    
    @Schema(description = "Document ID")
    private UUID documentId;
    
    @Schema(description = "Beneficiary ID", required = true)
    @NotNull(message = "Beneficiary ID is required")
    private UUID beneficiaryId;
    
    @Schema(description = "Document name", required = true)
    @NotBlank(message = "Document name is required")
    @Size(max = 200, message = "Document name must not exceed 200 characters")
    private String documentName;
    
    @Schema(description = "Document type: NATIONAL_ID, MEDICAL_REPORT, PRESCRIPTION, OTHER", required = true)
    @NotBlank(message = "Document type is required")
    private String documentType;
    
    @Schema(description = "Document description")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String documentDescription;
    
    @Schema(description = "File URL (storage location)", required = true)
    @NotBlank(message = "File URL is required")
    private String fileUrl;
    
    @Schema(description = "Original filename", required = true)
    @NotBlank(message = "File name is required")
    private String fileName;
    
    @Schema(description = "File size in bytes")
    private Long fileSizeBytes;
    
    @Schema(description = "MIME type")
    private String mimeType;
    
    @Schema(description = "Storage provider")
    private String storageProvider;
    
    @Schema(description = "Storage key")
    private String storageKey;
    
    @Schema(description = "Is active")
    private Boolean isActive;
    
    @Schema(description = "Created at")
    private Instant createdAt;
    
    @Schema(description = "Updated at")
    private Instant updatedAt;
}


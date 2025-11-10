package com.care.appointment.domain.model;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain model for Beneficiary Documents
 * 
 * Stores metadata about documents uploaded by beneficiaries (ID copies, medical reports, etc.)
 * File storage is handled externally (S3, etc.), this entity stores references.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryDocument {
    
    private UUID documentId;
    private UUID beneficiaryId;
    private String documentName;
    private String documentType; // NATIONAL_ID, MEDICAL_REPORT, PRESCRIPTION, OTHER
    private String documentDescription;
    private String fileUrl; // S3 or file storage URL
    private String fileName; // Original filename
    private Long fileSizeBytes;
    private String mimeType; // application/pdf, image/jpeg, etc.
    private String storageProvider; // S3, LOCAL, etc.
    private String storageKey; // Storage bucket/key
    
    private Boolean isActive;
    private Boolean isDeleted;
    private UUID createdById;
    private Instant createdAt;
    private Instant updatedAt;
    private Long rowVersion;
}


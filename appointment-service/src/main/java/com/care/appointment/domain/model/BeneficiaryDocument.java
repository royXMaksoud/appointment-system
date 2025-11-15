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
    private String legacyDocumentType;
    private UUID documentTypeCodeValueId;
    private String documentTypeCode;
    private String documentDescription;
    private String fileName; // Original filename
    private String fileExtension;
    private String filePath; // Relative path on storage
    private String fileUrl; // Legacy absolute url field (same as filePath)
    private Long fileSizeBytes;
    private String mimeType; // application/pdf, image/jpeg, etc.

    private Boolean isActive;
    private Boolean isDeleted;
    private UUID createdById;
    private UUID updatedById;
    private Instant createdAt;
    private Instant updatedAt;
    private Long rowVersion;
}


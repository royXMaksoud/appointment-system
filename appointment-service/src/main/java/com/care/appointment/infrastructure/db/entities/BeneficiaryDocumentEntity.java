package com.care.appointment.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing documents uploaded by beneficiaries
 */
@Entity
@Table(
    name = "beneficiary_documents",
    schema = "public",
    indexes = {
        @Index(name = "ix_beneficiary_docs_beneficiary", columnList = "beneficiary_id"),
        @Index(name = "ix_beneficiary_docs_type", columnList = "document_type"),
        @Index(name = "ix_beneficiary_docs_active", columnList = "is_active"),
        @Index(name = "ix_beneficiary_docs_deleted", columnList = "is_deleted")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class BeneficiaryDocumentEntity {

    @Id
    @UuidGenerator
    @Column(name = "document_id", nullable = false, updatable = false)
    private UUID documentId;

    @Column(name = "beneficiary_id", nullable = false)
    private UUID beneficiaryId;

    @Column(name = "document_name", nullable = false, length = 200)
    private String documentName;

    @Column(name = "document_type", nullable = false, length = 50)
    private String legacyDocumentType;

    @Column(name = "document_type_code_value_id", nullable = false)
    private UUID documentTypeCodeValueId;

    @Column(name = "document_type_code", length = 100)
    private String documentTypeCode;

    @Column(name = "document_description", length = 500)
    private String documentDescription;

    @Column(name = "file_name", nullable = false, length = 200)
    private String fileName;

    @Column(name = "file_extension", length = 20)
    private String fileExtension;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "created_by_user_id", nullable = false)
    private UUID createdById;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "updated_by_user_id")
    private UUID updatedById;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(name = "row_version")
    private Long rowVersion;

    @PrePersist
    void prePersist() {
        if (isActive == null) isActive = Boolean.TRUE;
        if (isDeleted == null) isDeleted = Boolean.FALSE;
        if (legacyDocumentType == null || legacyDocumentType.isBlank()) {
            legacyDocumentType = documentTypeCode != null ? documentTypeCode : "UNKNOWN";
        }
        if (fileUrl == null || fileUrl.isBlank()) {
            fileUrl = filePath;
        }
    }
}


package com.care.appointment.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing documents attached to appointments
 */
@Entity
@Table(
    name = "appointment_documents",
    schema = "public",
    indexes = {
        @Index(name = "ix_documents_appointment", columnList = "appointment_id"),
        @Index(name = "ix_documents_beneficiary", columnList = "beneficiary_id"),
        @Index(name = "ix_documents_type", columnList = "document_type"),
        @Index(name = "ix_documents_uploaded_at", columnList = "uploaded_at")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentDocumentEntity {

    @Id
    @UuidGenerator
    @Column(name = "document_id", nullable = false, updatable = false)
    private UUID documentId;

    /** NULL if document not yet attached to specific appointment */
    @Column(name = "appointment_id")
    private UUID appointmentId;

    @Column(name = "beneficiary_id", nullable = false)
    private UUID beneficiaryId;

    /** FAMILY_PHOTO, NATIONAL_ID, MEDICAL_REPORT, REFERRAL_LETTER, INSURANCE_CARD, OTHER */
    @Column(name = "document_type", nullable = false, length = 50)
    private String documentType;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    /** File size in bytes (max 10MB) */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "uploaded_by_user_id")
    private UUID uploadedByUserId;

    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false, nullable = false)
    private Instant uploadedAt;
}


package com.care.appointment.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity representing family members of beneficiaries
 */
@Entity
@Table(
    name = "family_members",
    schema = "public",
    indexes = {
        @Index(name = "ix_family_members_beneficiary", columnList = "beneficiary_id"),
        @Index(name = "ix_family_members_national_id", columnList = "national_id"),
        @Index(name = "ix_family_members_active", columnList = "is_active"),
        @Index(name = "ix_family_members_deleted", columnList = "is_deleted"),
        @Index(name = "ix_family_members_emergency", columnList = "is_emergency_contact")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class FamilyMemberEntity {

    @Id
    @UuidGenerator
    @Column(name = "family_member_id", nullable = false, updatable = false)
    private UUID familyMemberId;

    @Column(name = "beneficiary_id", nullable = false)
    private UUID beneficiaryId;

    @Column(name = "national_id", unique = true, length = 50)
    private String nationalId;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "mother_name", length = 200)
    private String motherName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "relation_type", nullable = false, length = 50)
    private String relationType;

    @Column(name = "relation_description", length = 200)
    private String relationDescription;

    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "gender_code_value_id")
    private UUID genderCodeValueId;

    @Column(name = "is_emergency_contact", nullable = false)
    private Boolean isEmergencyContact;

    @Column(name = "can_book_appointments", nullable = false)
    private Boolean canBookAppointments;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "created_by_user_id")
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
        if (isEmergencyContact == null) isEmergencyContact = Boolean.FALSE;
        if (canBookAppointments == null) canBookAppointments = Boolean.FALSE;
    }
}


package com.ftp.authservice.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "system_access_requests",
    indexes = {
        @Index(name = "ix_sar_user", columnList = "user_id"),
        @Index(name = "ix_sar_system", columnList = "system_id"),
        @Index(name = "ix_sar_status", columnList = "status"),
        @Index(name = "ix_sar_created", columnList = "created_at")
    },
    uniqueConstraints = {
        // prevent duplicate active request per (user, system) while pending
        @UniqueConstraint(name = "ux_sar_pending_unique", columnNames = {"user_id", "system_id", "status"})
    }
)
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SystemAccessRequestEntity {

    public enum RequestStatus { PENDING, APPROVED, REJECTED, CANCELED }
    public enum RequestedAccess { READ, MODIFY }

    @Id
    @Column(name = "system_access_request_id", nullable = false, updatable = false)
    private UUID systemAccessRequestId;

    @Column(name = "tenant_id")
    private UUID tenantId; // optional if you run multi-tenant

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "system_id", nullable = false)
    private UUID systemId;

    // what the user wants: READ / MODIFY
    @Enumerated(EnumType.STRING)
    @Column(name = "requested_access", nullable = false, length = 20)
    private RequestedAccess requestedAccess;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RequestStatus status;

    @Column(name = "reason", length = 1000)
    private String reason;

    // decision audit
    @Column(name = "decided_by_user_id")
    private UUID decidedByUserId;

    @Column(name = "decided_at")
    private Instant decidedAt;

    // optional effectivity of the grant (if approved)
    @Column(name = "effective_from")
    private Instant effectiveFrom;

    @Column(name = "effective_to")
    private Instant effectiveTo;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(name = "row_version")
    private Long rowVersion;

    @PrePersist
    void prePersist() {
        if (systemAccessRequestId == null) systemAccessRequestId = UUID.randomUUID();
        if (status == null) status = RequestStatus.PENDING;
    }
}

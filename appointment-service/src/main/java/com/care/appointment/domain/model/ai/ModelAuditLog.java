package com.care.appointment.domain.model.ai;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit trail for all model-related actions
 * Tracks deployment, rollbacks, training, and other operations
 */
@Entity
@Table(name = "model_audit_log", indexes = {
    @Index(name = "idx_audit_model_version", columnList = "model_version_id"),
    @Index(name = "idx_audit_action", columnList = "action"),
    @Index(name = "idx_audit_timestamp", columnList = "performed_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelAuditLog {

    @Id
    private UUID logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_version_id")
    private ModelVersion modelVersion;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private AuditAction action; // 'CREATED', 'TRAINED', 'DEPLOYED', 'ROLLED_BACK', 'ARCHIVED'

    @Column(name = "action_details", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode actionDetails;

    @Column(name = "performed_by_user_id")
    private UUID performedByUserId;

    @Column(nullable = false, updatable = false)
    private Instant performedAt;

    @PrePersist
    protected void onCreate() {
        logId = UUID.randomUUID();
        performedAt = Instant.now();
    }

    public enum AuditAction {
        CREATED, TRAINED, DEPLOYED, ROLLED_BACK, ARCHIVED, EVALUATED, COMPARED
    }
}

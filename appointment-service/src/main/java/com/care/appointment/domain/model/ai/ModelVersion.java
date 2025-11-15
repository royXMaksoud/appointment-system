package com.care.appointment.domain.model.ai;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents a specific version of the No-Show Prediction Model
 * Tracks metrics, algorithm details, and deployment status
 */
@Entity
@Table(name = "model_versions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"model_name", "version_number"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelVersion {

    @Id
    @Column(name = "model_version_id", nullable = false, updatable = false)
    private UUID modelVersionId;

    @Column(nullable = false)
    private String modelName;

    @Column(nullable = false)
    private Integer versionNumber;

    @Column(name = "algorithm_type")
    private String algorithmType; // 'RANDOM_FOREST', 'LOGISTIC_REGRESSION'

    // Performance Metrics
    @Column(precision = 5, scale = 2)
    private BigDecimal accuracy;

    @Column(precision = 5, scale = 2)
    private BigDecimal precision;

    @Column(precision = 5, scale = 2)
    private BigDecimal recall;

    @Column(name = "f1_score", precision = 5, scale = 2)
    private BigDecimal f1Score;

    @Column(name = "auc_roc", precision = 5, scale = 4)
    private BigDecimal aucRoc;

    // Training Data Info
    @Column(name = "training_data_count")
    private Integer trainingDataCount;

    @Column(name = "test_data_count")
    private Integer testDataCount;

    // Hyperparameters and Features (JSON)
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode hyperparameters;

    @Column(name = "feature_list", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode featureList;

    // Status Management
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ModelStatus status; // 'ACTIVE', 'ARCHIVED', 'TESTING'

    @Column(name = "deployment_timestamp")
    private Instant deploymentTimestamp;

    @Column(name = "deployed_by_user_id")
    private UUID deployedByUserId;

    // Audit Columns
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(name = "updated_by_user_id")
    private UUID updatedByUserId;

    @PrePersist
    protected void onCreate() {
        modelVersionId = UUID.randomUUID();
        createdAt = Instant.now();
        updatedAt = Instant.now();
        status = ModelStatus.TESTING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public enum ModelStatus {
        ACTIVE, ARCHIVED, TESTING
    }
}

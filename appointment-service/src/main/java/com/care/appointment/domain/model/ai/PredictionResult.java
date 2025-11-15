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
 * Represents a single prediction result for an appointment
 * Stores risk scores, confidence, contributing factors, and recommendations
 */
@Entity
@Table(name = "prediction_results", indexes = {
    @Index(name = "idx_prediction_results_appointment", columnList = "appointment_id"),
    @Index(name = "idx_prediction_results_model", columnList = "model_version_id"),
    @Index(name = "idx_prediction_results_risk_level", columnList = "predicted_risk_level"),
    @Index(name = "idx_prediction_results_timestamp", columnList = "prediction_timestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionResult {

    @Id
    private UUID predictionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_version_id")
    private ModelVersion modelVersion;

    @Column(name = "appointment_id")
    private UUID appointmentId;

    // Prediction Results
    @Column(name = "predicted_risk_score", precision = 5, scale = 4)
    private BigDecimal predictedRiskScore; // 0.00 to 1.00 (0-100%)

    @Column(name = "predicted_risk_level", length = 20)
    @Enumerated(EnumType.STRING)
    private RiskLevel predictedRiskLevel; // 'HIGH', 'MEDIUM', 'LOW'

    @Column(name = "confidence_score", precision = 5, scale = 4)
    private BigDecimal confidenceScore; // 0.0000 to 1.0000

    // Analysis Data (JSON)
    @Column(name = "contributing_factors", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode contributingFactors; // [{"factor": "previous_no_shows", "impact": 15}]

    @Column(name = "recommended_actions", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode recommendedActions; // ["send_sms_reminder", "call_beneficiary"]

    // Actual Outcome (populated later when appointment happens)
    @Column(name = "actual_outcome", length = 20)
    @Enumerated(EnumType.STRING)
    private ActualOutcome actualOutcome; // NULL (if future), 'NO_SHOW', 'SHOWED'

    @Column(name = "actual_outcome_timestamp")
    private Instant actualOutcomeTimestamp;

    // Audit
    @Column(nullable = false, updatable = false)
    private Instant predictionTimestamp;

    @Column(name = "predicted_by_user_id")
    private UUID predictedByUserId;

    @PrePersist
    protected void onCreate() {
        predictionId = UUID.randomUUID();
        predictionTimestamp = Instant.now();
    }

    public enum RiskLevel {
        HIGH, MEDIUM, LOW
    }

    public enum ActualOutcome {
        NO_SHOW, SHOWED
    }
}

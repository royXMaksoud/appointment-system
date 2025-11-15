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
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a training job for the No-Show Prediction Model
 * Tracks progress, status, and configuration of model training
 */
@Entity
@Table(name = "training_jobs", indexes = {
    @Index(name = "idx_training_jobs_status", columnList = "status"),
    @Index(name = "idx_training_jobs_model_version", columnList = "model_version_id"),
    @Index(name = "idx_training_jobs_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingJob {

    @Id
    private UUID jobId;

    @Column(name = "job_name")
    private String jobName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_version_id")
    private ModelVersion modelVersion;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TrainingStatus status; // 'PENDING', 'RUNNING', 'COMPLETED', 'FAILED', 'PAUSED'

    @Column(name = "progress_percentage")
    private Integer progressPercentage;

    // Timing
    @Column(name = "start_timestamp")
    private Instant startTimestamp;

    @Column(name = "end_timestamp")
    private Instant endTimestamp;

    @Column(name = "elapsed_seconds")
    private Long elapsedSeconds;

    @Column(name = "estimated_remaining_seconds")
    private Long estimatedRemainSeconds;

    // Data Configuration
    @Column(name = "data_range_from")
    private LocalDate dataRangeFrom;

    @Column(name = "data_range_to")
    private LocalDate dataRangeTo;

    @Column(name = "filter_criteria", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode filterCriteria; // { "centerIds": [...], "serviceTypes": [...] }

    // Results
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode results; // accuracy, precision, recall, f1_score, etc.

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    // Audit
    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        jobId = UUID.randomUUID();
        createdAt = Instant.now();
        status = TrainingStatus.PENDING;
        progressPercentage = 0;
    }

    public enum TrainingStatus {
        PENDING, RUNNING, COMPLETED, FAILED, PAUSED
    }
}

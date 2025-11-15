package com.care.appointment.domain.model.ai;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Tracks model performance metrics over time for comparison and trend analysis
 * Used for daily/weekly/monthly performance reports
 */
@Entity
@Table(name = "model_performance_metrics", indexes = {
    @Index(name = "idx_model_perf_model_version", columnList = "model_version_id"),
    @Index(name = "idx_model_perf_period", columnList = "period_date"),
    @Index(name = "idx_model_perf_period_type", columnList = "time_period")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelPerformanceMetrics {

    @Id
    private UUID metricId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_version_id")
    private ModelVersion modelVersion;

    @Column(name = "time_period", length = 20)
    @Enumerated(EnumType.STRING)
    private TimePeriod timePeriod; // 'DAILY', 'WEEKLY', 'MONTHLY'

    @Column(name = "period_date")
    private LocalDate periodDate;

    // Prediction Counts
    @Column(name = "total_predictions")
    private Integer totalPredictions;

    @Column(name = "correct_predictions")
    private Integer correctPredictions;

    @Column(name = "false_positives")
    private Integer falsePositives;

    @Column(name = "false_negatives")
    private Integer falseNegatives;

    // Actual vs Predicted Rates
    @Column(name = "actual_no_show_rate", precision = 5, scale = 2)
    private BigDecimal actualNoShowRate;

    @Column(name = "predicted_no_show_rate", precision = 5, scale = 2)
    private BigDecimal predictedNoShowRate;

    // Derived Metrics
    @Column(name = "sensitivity", precision = 5, scale = 4)
    private BigDecimal sensitivity; // True Positive Rate (Recall)

    @Column(name = "specificity", precision = 5, scale = 4)
    private BigDecimal specificity; // True Negative Rate

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        metricId = UUID.randomUUID();
        createdAt = Instant.now();
    }

    public enum TimePeriod {
        DAILY, WEEKLY, MONTHLY
    }
}

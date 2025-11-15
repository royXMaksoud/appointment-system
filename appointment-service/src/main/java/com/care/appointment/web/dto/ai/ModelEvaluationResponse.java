package com.care.appointment.web.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO containing complete model evaluation metrics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelEvaluationResponse {

    private UUID modelVersionId;
    private String modelVersion;
    private String algorithm;

    // Key Performance Indicators
    private BigDecimal accuracy;
    private BigDecimal precision;
    private BigDecimal recall;
    private BigDecimal f1Score;
    private BigDecimal aucRoc;
    private BigDecimal specificity;
    private BigDecimal sensitivity;

    // Confusion Matrix
    private ConfusionMatrix confusionMatrix;

    // Feature Importance
    private List<FeatureImportance> featureImportance;

    // Training Data Info
    private Integer totalSamples;
    private Integer trainingSamples;
    private Integer testSamples;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConfusionMatrix {
        private Long truePositives;
        private Long trueNegatives;
        private Long falsePositives;
        private Long falseNegatives;

        public Double getAccuracy() {
            long total = truePositives + trueNegatives + falsePositives + falseNegatives;
            if (total == 0) return 0.0;
            return (double) (truePositives + trueNegatives) / total;
        }

        public Double getPrecision() {
            long positives = truePositives + falsePositives;
            if (positives == 0) return 0.0;
            return (double) truePositives / positives;
        }

        public Double getRecall() {
            long actualPositives = truePositives + falseNegatives;
            if (actualPositives == 0) return 0.0;
            return (double) truePositives / actualPositives;
        }

        public Double getF1Score() {
            Double precision = getPrecision();
            Double recall = getRecall();
            if (precision + recall == 0) return 0.0;
            return 2 * (precision * recall) / (precision + recall);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FeatureImportance {
        private String featureName;
        private BigDecimal importance; // 0.0 to 1.0
        private Integer rankingOrder;
    }
}

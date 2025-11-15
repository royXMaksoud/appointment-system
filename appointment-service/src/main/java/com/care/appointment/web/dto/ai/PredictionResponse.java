package com.care.appointment.web.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for prediction results
 * Contains risk assessment, contributing factors, and recommendations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionResponse {

    private UUID predictionId;
    private UUID appointmentId;

    // Risk Assessment
    private BigDecimal riskScore; // 0.00 to 1.00
    private String riskLevel; // HIGH, MEDIUM, LOW
    private BigDecimal confidence; // 0.0000 to 1.0000

    // Contributing Factors
    private List<ContributingFactor> contributingFactors;

    // Recommendations
    private List<String> recommendedActions;

    // Metadata
    private Instant predictionTimestamp;
    private String modelVersion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContributingFactor {
        private String factor;
        private String value;
        private Integer impactPercent;
        private String description;
    }
}

package com.care.appointment.web.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response DTO for training job status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingJobResponse {

    private UUID jobId;
    private String jobName;
    private String status; // PENDING, RUNNING, COMPLETED, FAILED, PAUSED
    private Integer progressPercentage; // 0-100

    // Timing
    private Instant startTimestamp;
    private Instant endTimestamp;
    private Long elapsedSeconds;
    private Long estimatedRemainSeconds;

    // Configuration
    private LocalDate dataRangeFrom;
    private LocalDate dataRangeTo;
    private String algorithm;

    // Results (populated when completed)
    private ModelTrainingResults results;

    // Audit
    private Instant createdAt;
    private UUID createdByUserId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ModelTrainingResults {
        private Double accuracy;
        private Double precision;
        private Double recall;
        private Double f1Score;
        private Double aucRoc;
        private Integer trainingDataCount;
        private Integer testDataCount;
        private String modelVersionId;
        private Integer modelVersionNumber;
    }
}

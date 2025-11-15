package com.care.appointment.web.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for starting a new model training job
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingRequest {

    // Data Range
    private LocalDate dateRangeFrom;
    private LocalDate dateRangeTo;

    // Filters
    private List<UUID> centerIds;
    private List<UUID> serviceTypeIds;
    private Integer minHistoricalAppointments; // Minimum appointments per beneficiary

    // Training Configuration
    private String algorithm; // RANDOM_FOREST, LOGISTIC_REGRESSION
    private Double trainTestSplit; // 0.7 = 70% train, 30% test
    private List<String> features; // Features to use in training

    // Hyperparameters
    private Map<String, Object> hyperparameters; // Algorithm-specific params
    // Example for Random Forest:
    // {
    //   "maxDepth": 10,
    //   "numTrees": 100,
    //   "minSamplesSplit": 5
    // }

    public String getJobName() {
        return String.format("Training_%s_%s_to_%s",
            algorithm,
            dateRangeFrom,
            dateRangeTo);
    }
}

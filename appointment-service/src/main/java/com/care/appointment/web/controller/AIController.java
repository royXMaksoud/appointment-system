package com.care.appointment.web.controller;

import com.care.appointment.application.ai.service.ModelTrainingService;
import com.care.appointment.application.ai.service.NoShowPredictionService;
import com.care.appointment.web.dto.ai.ModelEvaluationResponse;
import com.care.appointment.web.dto.ai.PredictionRequest;
import com.care.appointment.web.dto.ai.PredictionResponse;
import com.care.appointment.web.dto.ai.TrainingJobResponse;
import com.care.appointment.web.dto.ai.TrainingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for AI/ML features
 * Handles appointment predictions, model management, and training operations
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI & Predictions", description = "No-show prediction and model management APIs")
public class AIController {

    private final NoShowPredictionService predictionService;
    private final ModelTrainingService trainingService;

    /**
     * Predict no-show risk for an appointment
     * @param appointmentId UUID of the appointment to predict
     * @return Prediction result with risk score and recommendations
     */
    @GetMapping("/predictions/appointment/{appointmentId}")
    @Operation(summary = "Predict no-show risk for appointment",
               description = "Get no-show risk assessment for a specific appointment")
    public ResponseEntity<PredictionResponse> predictAppointment(
        @Parameter(description = "Appointment ID")
        @PathVariable UUID appointmentId) {

        log.info("GET /api/ai/predictions/appointment/{}", appointmentId);
        PredictionResponse response = predictionService.predict(appointmentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Predict with manually provided features
     * @param request Request containing appointment features
     * @return Prediction result
     */
    @PostMapping("/predictions/predict")
    @Operation(summary = "Predict with manual features",
               description = "Get no-show risk prediction with manually entered features")
    public ResponseEntity<PredictionResponse> predictWithFeatures(
        @RequestBody PredictionRequest request) {

        log.info("POST /api/ai/predictions/predict - Algorithm: manual features");

        if (!request.isUsingManualFeatures()) {
            return ResponseEntity.badRequest().build();
        }

        PredictionResponse response = predictionService.predictWithManualFeatures(request);
        return ResponseEntity.ok(response);
    }

    // ============================================================================
    // TRAINING ENDPOINTS
    // ============================================================================

    /**
     * Start a new model training job
     * @param request Training configuration
     * @return Training job details with ID for tracking
     */
    @PostMapping("/training/start")
    @Operation(summary = "Start model training",
               description = "Initiate a new training job for the no-show prediction model")
    public ResponseEntity<TrainingJobResponse> startTraining(
        @RequestBody TrainingRequest request) {

        log.info("POST /api/ai/training/start - Algorithm: {}", request.getAlgorithm());
        TrainingJobResponse response = trainingService.startTraining(request);
        return ResponseEntity.accepted().body(response);
    }

    /**
     * Get training job status
     * @param jobId ID of training job to check
     * @return Current status and progress
     */
    @GetMapping("/training/status/{jobId}")
    @Operation(summary = "Get training job status",
               description = "Check the progress and status of a running training job")
    public ResponseEntity<TrainingJobResponse> getTrainingStatus(
        @Parameter(description = "Training Job ID")
        @PathVariable UUID jobId) {

        log.info("GET /api/ai/training/status/{}", jobId);
        TrainingJobResponse response = trainingService.getTrainingStatus(jobId);
        return ResponseEntity.ok(response);
    }

    /**
     * Evaluate a trained model
     * @param modelVersionId ID of model to evaluate
     * @return Complete evaluation metrics
     */
    @GetMapping("/models/{modelVersionId}/evaluate")
    @Operation(summary = "Evaluate model performance",
               description = "Get detailed evaluation metrics for a specific model version")
    public ResponseEntity<ModelEvaluationResponse> evaluateModel(
        @Parameter(description = "Model Version ID")
        @PathVariable UUID modelVersionId) {

        log.info("GET /api/ai/models/{}/evaluate", modelVersionId);
        ModelEvaluationResponse response = trainingService.evaluateModel(modelVersionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check for AI service
     */
    @GetMapping("/health")
    @Operation(summary = "AI Service Health Check",
               description = "Check if AI service is running and models are loaded")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(HealthResponse.builder()
            .status("UP")
            .message("AI service is operational")
            .build());
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    public static class HealthResponse {
        private String status;
        private String message;
    }
}

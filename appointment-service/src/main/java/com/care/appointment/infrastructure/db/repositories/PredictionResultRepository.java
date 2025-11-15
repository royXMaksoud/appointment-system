package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.domain.model.ai.PredictionResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PredictionResultRepository extends JpaRepository<PredictionResult, UUID> {

    /**
     * Find prediction for a specific appointment
     */
    Optional<PredictionResult> findByAppointmentId(UUID appointmentId);

    /**
     * Find all predictions for an appointment (history)
     */
    List<PredictionResult> findByAppointmentIdOrderByPredictionTimestampDesc(UUID appointmentId);

    /**
     * Find predictions by risk level
     */
    List<PredictionResult> findByPredictedRiskLevel(PredictionResult.RiskLevel riskLevel);

    /**
     * Find predictions for a specific date
     */
    @Query("SELECT p FROM PredictionResult p WHERE DATE(p.predictionTimestamp) = :date")
    List<PredictionResult> findByPredictionDate(@Param("date") LocalDate date);

    /**
     * Find high-risk predictions for a specific date
     */
    @Query("SELECT p FROM PredictionResult p WHERE DATE(p.predictionTimestamp) = :date " +
           "AND p.predictedRiskLevel = 'HIGH' ORDER BY p.predictedRiskScore DESC")
    List<PredictionResult> findHighRiskPredictionsForDate(@Param("date") LocalDate date);

    /**
     * Find predictions by model version
     */
    @Query("SELECT p FROM PredictionResult p WHERE p.modelVersion.modelVersionId = :modelVersionId ORDER BY p.predictionTimestamp DESC")
    List<PredictionResult> findByModelVersion(@Param("modelVersionId") UUID modelVersionId);

    /**
     * Count predictions by risk level
     */
    long countByPredictedRiskLevel(PredictionResult.RiskLevel riskLevel);

    /**
     * Find predictions with actual outcomes (for model evaluation)
     */
    @Query("SELECT p FROM PredictionResult p WHERE p.actualOutcome IS NOT NULL")
    List<PredictionResult> findWithActualOutcomes();

    /**
     * Find predictions with actual outcomes for a specific model version
     */
    @Query("SELECT p FROM PredictionResult p WHERE p.modelVersion.modelVersionId = :modelVersionId " +
           "AND p.actualOutcome IS NOT NULL")
    List<PredictionResult> findWithActualOutcomesByModel(@Param("modelVersionId") UUID modelVersionId);

    /**
     * Paginated search for predictions
     */
    @Query("SELECT p FROM PredictionResult p WHERE p.predictionTimestamp BETWEEN :startTime AND :endTime " +
           "ORDER BY p.predictionTimestamp DESC")
    Page<PredictionResult> findByDateRange(@Param("startTime") Instant startTime,
                                          @Param("endTime") Instant endTime,
                                          Pageable pageable);

    /**
     * Count total predictions for a date range
     */
    @Query("SELECT COUNT(p) FROM PredictionResult p WHERE p.predictionTimestamp BETWEEN :startTime AND :endTime")
    long countByDateRange(@Param("startTime") Instant startTime, @Param("endTime") Instant endTime);

    /**
     * Calculate distribution of predictions by risk level
     */
    @Query("SELECT p.predictedRiskLevel, COUNT(p) FROM PredictionResult p " +
           "WHERE DATE(p.predictionTimestamp) = :date GROUP BY p.predictedRiskLevel")
    List<Object[]> getRiskDistributionForDate(@Param("date") LocalDate date);
}

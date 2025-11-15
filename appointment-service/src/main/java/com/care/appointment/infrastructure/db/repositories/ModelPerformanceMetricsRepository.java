package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.domain.model.ai.ModelPerformanceMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ModelPerformanceMetricsRepository extends JpaRepository<ModelPerformanceMetrics, UUID> {

    /**
     * Find all metrics for a specific model version
     */
    List<ModelPerformanceMetrics> findByModelVersionOrderByPeriodDateDesc(
        com.care.appointment.domain.model.ai.ModelVersion modelVersion);

    /**
     * Find metrics for a specific model version and time period
     */
    List<ModelPerformanceMetrics> findByModelVersionAndTimePeriodOrderByPeriodDateDesc(
        com.care.appointment.domain.model.ai.ModelVersion modelVersion,
        ModelPerformanceMetrics.TimePeriod timePeriod);

    /**
     * Find metrics for a specific date
     */
    List<ModelPerformanceMetrics> findByPeriodDateOrderByCreatedAtDesc(LocalDate date);

    /**
     * Find metrics in a date range for a specific model
     */
    @Query("SELECT m FROM ModelPerformanceMetrics m WHERE m.modelVersion.modelVersionId = :modelVersionId " +
           "AND m.periodDate BETWEEN :startDate AND :endDate ORDER BY m.periodDate DESC")
    List<ModelPerformanceMetrics> findMetricsInDateRange(@Param("modelVersionId") UUID modelVersionId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);

    /**
     * Get the most recent daily metrics
     */
    @Query("SELECT m FROM ModelPerformanceMetrics m WHERE m.modelVersion.modelVersionId = :modelVersionId " +
           "AND m.timePeriod = 'DAILY' ORDER BY m.periodDate DESC LIMIT 1")
    ModelPerformanceMetrics findMostRecentDailyMetrics(@Param("modelVersionId") UUID modelVersionId);

    /**
     * Compare metrics between two model versions on the same date
     */
    @Query("SELECT m FROM ModelPerformanceMetrics m WHERE m.periodDate = :date " +
           "AND m.modelVersion.modelVersionId IN (:modelVersionIds) ORDER BY m.modelVersion.versionNumber DESC")
    List<ModelPerformanceMetrics> compareModelMetrics(@Param("date") LocalDate date,
                                                      @Param("modelVersionIds") List<UUID> modelVersionIds);
}

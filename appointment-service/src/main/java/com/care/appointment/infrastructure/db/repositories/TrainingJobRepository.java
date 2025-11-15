package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.domain.model.ai.TrainingJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainingJobRepository extends JpaRepository<TrainingJob, UUID> {

    /**
     * Find all training jobs ordered by creation date descending
     */
    List<TrainingJob> findAllByOrderByCreatedAtDesc();

    /**
     * Find training jobs by status
     */
    List<TrainingJob> findByStatus(TrainingJob.TrainingStatus status);

    /**
     * Find running training jobs (only one should be running at a time)
     */
    @Query("SELECT t FROM TrainingJob t WHERE t.status IN ('PENDING', 'RUNNING')")
    List<TrainingJob> findRunningJobs();

    /**
     * Find training jobs for a specific model version
     */
    List<TrainingJob> findByModelVersionOrderByCreatedAtDesc(com.care.appointment.domain.model.ai.ModelVersion modelVersion);

    /**
     * Find the most recent completed training job
     */
    @Query("SELECT t FROM TrainingJob t WHERE t.status = 'COMPLETED' ORDER BY t.endTimestamp DESC LIMIT 1")
    Optional<TrainingJob> findMostRecentCompletedJob();

    /**
     * Find jobs created in a date range
     */
    @Query("SELECT t FROM TrainingJob t WHERE t.createdAt BETWEEN :startTime AND :endTime ORDER BY t.createdAt DESC")
    List<TrainingJob> findJobsByDateRange(@Param("startTime") Instant startTime, @Param("endTime") Instant endTime);

    /**
     * Paginated query for job history
     */
    Page<TrainingJob> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

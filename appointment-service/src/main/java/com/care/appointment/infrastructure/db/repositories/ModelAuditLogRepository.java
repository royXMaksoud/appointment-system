package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.domain.model.ai.ModelAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ModelAuditLogRepository extends JpaRepository<ModelAuditLog, UUID> {

    /**
     * Find all audit logs for a specific model version
     */
    List<ModelAuditLog> findByModelVersionOrderByPerformedAtDesc(
        com.care.appointment.domain.model.ai.ModelVersion modelVersion);

    /**
     * Find specific type of actions
     */
    List<ModelAuditLog> findByActionOrderByPerformedAtDesc(ModelAuditLog.AuditAction action);

    /**
     * Find deployment actions
     */
    @Query("SELECT l FROM ModelAuditLog l WHERE l.action = 'DEPLOYED' ORDER BY l.performedAt DESC")
    List<ModelAuditLog> findDeploymentHistory();

    /**
     * Find rollback actions
     */
    @Query("SELECT l FROM ModelAuditLog l WHERE l.action = 'ROLLED_BACK' ORDER BY l.performedAt DESC")
    List<ModelAuditLog> findRollbackHistory();

    /**
     * Find audit logs for a model version and action type
     */
    @Query("SELECT l FROM ModelAuditLog l WHERE l.modelVersion.modelVersionId = :modelVersionId " +
           "AND l.action = :action ORDER BY l.performedAt DESC")
    List<ModelAuditLog> findByModelAndAction(@Param("modelVersionId") UUID modelVersionId,
                                             @Param("action") ModelAuditLog.AuditAction action);

    /**
     * Find audit logs in a time range
     */
    @Query("SELECT l FROM ModelAuditLog l WHERE l.performedAt BETWEEN :startTime AND :endTime " +
           "ORDER BY l.performedAt DESC")
    List<ModelAuditLog> findByTimeRange(@Param("startTime") Instant startTime, @Param("endTime") Instant endTime);

    /**
     * Paginated audit log search
     */
    Page<ModelAuditLog> findAllByOrderByPerformedAtDesc(Pageable pageable);
}

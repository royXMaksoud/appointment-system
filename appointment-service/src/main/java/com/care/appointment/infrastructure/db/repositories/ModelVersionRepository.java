package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.domain.model.ai.ModelVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModelVersionRepository extends JpaRepository<ModelVersion, UUID> {

    /**
     * Find all versions of a model ordered by version number descending
     */
    @Query("SELECT m FROM ModelVersion m WHERE m.modelName = :modelName ORDER BY m.versionNumber DESC")
    List<ModelVersion> findAllVersionsByModelName(@Param("modelName") String modelName);

    /**
     * Find the currently active model
     */
    @Query("SELECT m FROM ModelVersion m WHERE m.status = 'ACTIVE' ORDER BY m.deploymentTimestamp DESC LIMIT 1")
    Optional<ModelVersion> findActiveModel();

    /**
     * Find a specific version by model name and version number
     */
    Optional<ModelVersion> findByModelNameAndVersionNumber(String modelName, Integer versionNumber);

    /**
     * Find all active models
     */
    List<ModelVersion> findByStatus(ModelVersion.ModelStatus status);

    /**
     * Find models deployed after a certain timestamp
     */
    @Query("SELECT m FROM ModelVersion m WHERE m.deploymentTimestamp > :afterTime ORDER BY m.deploymentTimestamp DESC")
    List<ModelVersion> findRecentDeployments(@Param("afterTime") java.time.Instant afterTime);

    /**
     * Paginated query for history view
     */
    @Query("SELECT m FROM ModelVersion m WHERE m.modelName = :modelName ORDER BY m.versionNumber DESC")
    Page<ModelVersion> findVersionHistory(@Param("modelName") String modelName, Pageable pageable);
}

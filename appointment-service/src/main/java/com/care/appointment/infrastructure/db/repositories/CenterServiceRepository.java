package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.CenterServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CenterServiceRepository extends 
        JpaRepository<CenterServiceEntity, UUID>,
        JpaSpecificationExecutor<CenterServiceEntity> {

    List<CenterServiceEntity> findByOrganizationBranchIdInAndIsActiveTrue(List<UUID> organizationBranchIds);

    List<CenterServiceEntity> findByOrganizationBranchIdAndIsActiveTrue(UUID organizationBranchId);

    List<CenterServiceEntity> findByOrganizationBranchId(UUID organizationBranchId);
    
    List<CenterServiceEntity> findByServiceTypeIdAndIsActiveTrue(UUID serviceTypeId);
    
    Optional<CenterServiceEntity> findByOrganizationBranchIdAndServiceTypeId(
        UUID organizationBranchId, UUID serviceTypeId);
    
    boolean existsByOrganizationBranchIdAndServiceTypeId(
        UUID organizationBranchId, UUID serviceTypeId);
    
    @Query("SELECT cs.organizationBranchId FROM CenterServiceEntity cs " +
           "WHERE cs.serviceTypeId = :serviceTypeId AND cs.isActive = true")
    List<UUID> findBranchIdsByServiceTypeId(@Param("serviceTypeId") UUID serviceTypeId);
    
    @Query("SELECT cs.serviceTypeId FROM CenterServiceEntity cs " +
           "WHERE cs.organizationBranchId = :branchId AND cs.isActive = true")
    List<UUID> findServiceTypeIdsByBranchId(@Param("branchId") UUID branchId);
}


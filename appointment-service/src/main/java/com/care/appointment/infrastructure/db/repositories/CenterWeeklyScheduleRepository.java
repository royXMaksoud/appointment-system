package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.CenterWeeklyScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CenterWeeklyScheduleRepository extends 
        JpaRepository<CenterWeeklyScheduleEntity, UUID>,
        JpaSpecificationExecutor<CenterWeeklyScheduleEntity> {
    
    List<CenterWeeklyScheduleEntity> findByOrganizationBranchIdAndIsActiveTrue(UUID organizationBranchId);
    
    Optional<CenterWeeklyScheduleEntity> findByOrganizationBranchIdAndDayOfWeek(
        UUID organizationBranchId, Integer dayOfWeek);
    
    Optional<CenterWeeklyScheduleEntity> findByOrganizationBranchIdAndDayOfWeekAndIsActiveTrue(
        UUID organizationBranchId, Integer dayOfWeek);
    
    boolean existsByOrganizationBranchIdAndDayOfWeek(
        UUID organizationBranchId, Integer dayOfWeek);
    
    boolean existsByOrganizationBranchIdAndDayOfWeekAndIsDeletedFalse(
        UUID organizationBranchId, Integer dayOfWeek);
}


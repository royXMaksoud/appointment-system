package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.CenterDailyCapacityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CenterDailyCapacityRepository extends 
        JpaRepository<CenterDailyCapacityEntity, UUID>,
        JpaSpecificationExecutor<CenterDailyCapacityEntity> {
    
    Optional<CenterDailyCapacityEntity> findByOrganizationBranchIdAndCapacityDateAndServiceTypeId(
        UUID organizationBranchId, LocalDate capacityDate, UUID serviceTypeId);
    
    Optional<CenterDailyCapacityEntity> findByOrganizationBranchIdAndCapacityDateAndServiceTypeIdIsNull(
        UUID organizationBranchId, LocalDate capacityDate);
    
    List<CenterDailyCapacityEntity> findByOrganizationBranchIdAndCapacityDateBetween(
        UUID organizationBranchId, LocalDate startDate, LocalDate endDate);
    
    List<CenterDailyCapacityEntity> findByOrganizationBranchIdAndCapacityDate(
        UUID organizationBranchId, LocalDate capacityDate);
    
    @Modifying
    @Query("UPDATE CenterDailyCapacityEntity c SET c.availableSlots = c.availableSlots - 1 " +
           "WHERE c.organizationBranchId = :branchId AND c.capacityDate = :date " +
           "AND c.availableSlots > 0")
    int decrementAvailableSlots(@Param("branchId") UUID branchId, @Param("date") LocalDate date);
    
    @Modifying
    @Query("UPDATE CenterDailyCapacityEntity c SET c.availableSlots = c.availableSlots + 1 " +
           "WHERE c.organizationBranchId = :branchId AND c.capacityDate = :date " +
           "AND c.availableSlots < c.totalSlots")
    int incrementAvailableSlots(@Param("branchId") UUID branchId, @Param("date") LocalDate date);
}


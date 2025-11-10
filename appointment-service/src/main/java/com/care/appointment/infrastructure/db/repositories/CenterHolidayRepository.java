package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.CenterHolidayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CenterHolidayRepository extends 
        JpaRepository<CenterHolidayEntity, UUID>,
        JpaSpecificationExecutor<CenterHolidayEntity> {
    
    List<CenterHolidayEntity> findByOrganizationBranchId(UUID organizationBranchId);
    
    List<CenterHolidayEntity> findByOrganizationBranchIdAndHolidayDateBetween(
        UUID organizationBranchId, LocalDate startDate, LocalDate endDate);
    
    Optional<CenterHolidayEntity> findByOrganizationBranchIdAndHolidayDate(
        UUID organizationBranchId, LocalDate holidayDate);
    
    boolean existsByOrganizationBranchIdAndHolidayDate(
        UUID organizationBranchId, LocalDate holidayDate);
    
    boolean existsByOrganizationBranchIdAndHolidayDateAndIsDeletedFalse(
        UUID organizationBranchId, LocalDate holidayDate);
    
    List<CenterHolidayEntity> findByIsRecurringYearlyTrue();
}


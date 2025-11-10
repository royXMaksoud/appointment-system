package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends 
        JpaRepository<AppointmentEntity, UUID>,
        JpaSpecificationExecutor<AppointmentEntity> {
    
    List<AppointmentEntity> findByBeneficiaryIdOrderByAppointmentDateDescAppointmentTimeDesc(UUID beneficiaryId);
    
    List<AppointmentEntity> findByOrganizationBranchIdAndAppointmentDate(
        UUID organizationBranchId, LocalDate appointmentDate);
    
    List<AppointmentEntity> findByOrganizationBranchIdAndAppointmentDateBetween(
        UUID organizationBranchId, LocalDate startDate, LocalDate endDate);
    
    List<AppointmentEntity> findByAppointmentStatusId(UUID appointmentStatusId);
    
    Optional<AppointmentEntity> findByOrganizationBranchIdAndAppointmentDateAndAppointmentTime(
        UUID organizationBranchId, LocalDate appointmentDate, LocalTime appointmentTime);
    
    boolean existsByOrganizationBranchIdAndAppointmentDateAndAppointmentTime(
        UUID organizationBranchId, LocalDate appointmentDate, LocalTime appointmentTime);
    
    @Query("SELECT COUNT(a) FROM AppointmentEntity a WHERE a.organizationBranchId = :branchId " +
           "AND a.appointmentDate = :date")
    long countByBranchAndDate(@Param("branchId") UUID branchId, @Param("date") LocalDate date);
    
    @Query("SELECT a FROM AppointmentEntity a WHERE a.appointmentDate = :date " +
           "AND a.appointmentStatusId IN :statusIds ORDER BY a.appointmentTime ASC")
    List<AppointmentEntity> findByDateAndStatuses(
        @Param("date") LocalDate date, @Param("statusIds") List<UUID> statusIds);
    
    @Query("SELECT a FROM AppointmentEntity a WHERE a.beneficiaryId = :beneficiaryId " +
           "AND a.appointmentDate >= :fromDate ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
    List<AppointmentEntity> findUpcomingAppointments(
        @Param("beneficiaryId") UUID beneficiaryId, @Param("fromDate") LocalDate fromDate);
    
    /**
     * Check if beneficiary has an appointment for same service type and date
     * Prevents duplicate bookings for same person, same service, same day
     */
    @Query("SELECT COUNT(a) > 0 FROM AppointmentEntity a WHERE a.beneficiaryId = :beneficiaryId " +
           "AND a.serviceTypeId = :serviceTypeId AND a.appointmentDate = :appointmentDate " +
           "AND a.appointmentStatusId NOT IN :excludedStatusIds")
    boolean hasExistingAppointmentForServiceAndDate(
        @Param("beneficiaryId") UUID beneficiaryId,
        @Param("serviceTypeId") UUID serviceTypeId,
        @Param("appointmentDate") LocalDate appointmentDate,
        @Param("excludedStatusIds") List<UUID> excludedStatusIds);
    
    /**
     * Check if beneficiary has an appointment for same service type with status NOT in excluded list
     * Prevents booking same service type multiple times unless previous appointments are CLOSED/CANCELLED
     */
    @Query("SELECT COUNT(a) > 0 FROM AppointmentEntity a WHERE a.beneficiaryId = :beneficiaryId " +
           "AND a.serviceTypeId = :serviceTypeId " +
           "AND a.appointmentStatusId NOT IN :excludedStatusIds")
    boolean hasActiveAppointmentForService(
        @Param("beneficiaryId") UUID beneficiaryId,
        @Param("serviceTypeId") UUID serviceTypeId,
        @Param("excludedStatusIds") List<UUID> excludedStatusIds);
}

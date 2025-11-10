package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.AppointmentTransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentTransferRepository extends 
        JpaRepository<AppointmentTransferEntity, UUID>,
        JpaSpecificationExecutor<AppointmentTransferEntity> {
    
    List<AppointmentTransferEntity> findByAppointmentId(UUID appointmentId);
    
    List<AppointmentTransferEntity> findByFromOrganizationBranchIdAndStatus(
        UUID fromBranchId, String status);
    
    List<AppointmentTransferEntity> findByToOrganizationBranchIdAndStatus(
        UUID toBranchId, String status);
    
    Optional<AppointmentTransferEntity> findByAppointmentIdAndStatus(
        UUID appointmentId, String status);
    
    @Query("SELECT t FROM AppointmentTransferEntity t WHERE " +
           "(t.fromOrganizationBranchId = :branchId OR t.toOrganizationBranchId = :branchId) " +
           "AND t.status = :status ORDER BY t.transferredAt DESC")
    List<AppointmentTransferEntity> findByBranchAndStatus(
        @Param("branchId") UUID branchId, @Param("status") String status);
    
    long countByToOrganizationBranchIdAndStatus(UUID toBranchId, String status);
}


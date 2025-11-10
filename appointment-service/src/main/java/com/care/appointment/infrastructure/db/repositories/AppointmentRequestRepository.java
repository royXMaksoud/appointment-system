package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.AppointmentRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRequestRepository extends 
        JpaRepository<AppointmentRequestEntity, UUID>,
        JpaSpecificationExecutor<AppointmentRequestEntity> {
    
    List<AppointmentRequestEntity> findByBeneficiaryId(UUID beneficiaryId);
    
    List<AppointmentRequestEntity> findByBeneficiaryIdOrderByCreatedAtDesc(UUID beneficiaryId);
    
    List<AppointmentRequestEntity> findByStatus(String status);
    
    List<AppointmentRequestEntity> findByServiceTypeIdAndStatus(UUID serviceTypeId, String status);
    
    @Query("SELECT ar FROM AppointmentRequestEntity ar WHERE ar.status = :status " +
           "AND ar.createdAt >= :since ORDER BY ar.priority DESC, ar.createdAt ASC")
    List<AppointmentRequestEntity> findPendingRequestsSince(
        @Param("status") String status, @Param("since") Instant since);
    
    long countByStatusAndCreatedAtAfter(String status, Instant createdAt);
}


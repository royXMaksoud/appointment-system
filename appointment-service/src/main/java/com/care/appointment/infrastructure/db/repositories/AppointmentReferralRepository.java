package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.AppointmentReferralEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentReferralRepository extends 
        JpaRepository<AppointmentReferralEntity, UUID>,
        JpaSpecificationExecutor<AppointmentReferralEntity> {
    
    List<AppointmentReferralEntity> findByAppointmentId(UUID appointmentId);
    
    List<AppointmentReferralEntity> findByBeneficiaryId(UUID beneficiaryId);
    
    List<AppointmentReferralEntity> findByReferredToAppointmentId(UUID appointmentId);
    
    List<AppointmentReferralEntity> findByStatus(String status);
    
    List<AppointmentReferralEntity> findByStatusAndIsUrgentTrue(String status);
    
    List<AppointmentReferralEntity> findByBeneficiaryIdAndStatus(UUID beneficiaryId, String status);
    
    Optional<AppointmentReferralEntity> findByReferredToAppointmentIdAndStatus(UUID appointmentId, String status);

    void deleteByAppointmentId(UUID appointmentId);
}


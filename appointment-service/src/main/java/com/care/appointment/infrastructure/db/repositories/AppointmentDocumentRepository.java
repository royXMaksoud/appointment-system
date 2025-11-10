package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.AppointmentDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentDocumentRepository extends JpaRepository<AppointmentDocumentEntity, UUID> {
    
    List<AppointmentDocumentEntity> findByAppointmentId(UUID appointmentId);
    
    List<AppointmentDocumentEntity> findByBeneficiaryId(UUID beneficiaryId);
    
    List<AppointmentDocumentEntity> findByBeneficiaryIdAndDocumentType(
        UUID beneficiaryId, String documentType);
    
    List<AppointmentDocumentEntity> findByAppointmentIdAndDocumentType(
        UUID appointmentId, String documentType);
    
    long countByAppointmentId(UUID appointmentId);
    
    long countByBeneficiaryId(UUID beneficiaryId);
}


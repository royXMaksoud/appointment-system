package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.AppointmentStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentStatusHistoryRepository extends JpaRepository<AppointmentStatusHistoryEntity, UUID> {
    
    List<AppointmentStatusHistoryEntity> findByAppointmentIdOrderByChangedAtDesc(UUID appointmentId);
    
    List<AppointmentStatusHistoryEntity> findByAppointmentStatusId(UUID appointmentStatusId);
    
    List<AppointmentStatusHistoryEntity> findByChangedByUserId(UUID userId);
}


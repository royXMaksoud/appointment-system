package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.AppointmentStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentStatusRepository extends JpaRepository<AppointmentStatusEntity, UUID> {
    
    Optional<AppointmentStatusEntity> findByCode(String code);
    
    List<AppointmentStatusEntity> findByIsActiveTrue();
    
    boolean existsByCode(String code);
}


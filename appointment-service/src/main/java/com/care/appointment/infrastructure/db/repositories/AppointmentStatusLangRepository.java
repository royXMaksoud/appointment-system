package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.AppointmentStatusLangEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentStatusLangRepository extends JpaRepository<AppointmentStatusLangEntity, UUID>,
        JpaSpecificationExecutor<AppointmentStatusLangEntity> {
    
    List<AppointmentStatusLangEntity> findByAppointmentStatusId(UUID appointmentStatusId);
    
    Optional<AppointmentStatusLangEntity> findByAppointmentStatusIdAndLanguageCodeIgnoreCase(
        UUID appointmentStatusId, String languageCode);
    
    List<AppointmentStatusLangEntity> findByLanguageCodeAndIsActiveTrue(String languageCode);
}


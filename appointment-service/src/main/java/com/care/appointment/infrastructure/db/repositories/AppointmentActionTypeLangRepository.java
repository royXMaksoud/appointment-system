package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.AppointmentActionTypeLangEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentActionTypeLangRepository extends JpaRepository<AppointmentActionTypeLangEntity, UUID> {
    
    List<AppointmentActionTypeLangEntity> findByActionTypeId(UUID actionTypeId);
    
    Optional<AppointmentActionTypeLangEntity> findByActionTypeIdAndLanguageCode(
        UUID actionTypeId, String languageCode);
    
    List<AppointmentActionTypeLangEntity> findByLanguageCodeAndIsActiveTrue(String languageCode);
}


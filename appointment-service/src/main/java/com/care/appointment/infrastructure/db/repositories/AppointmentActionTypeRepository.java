package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.AppointmentActionTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentActionTypeRepository extends 
        JpaRepository<AppointmentActionTypeEntity, UUID>,
        JpaSpecificationExecutor<AppointmentActionTypeEntity> {
    
    Optional<AppointmentActionTypeEntity> findByCode(String code);
    
    List<AppointmentActionTypeEntity> findByIsActiveTrue();
    
    boolean existsByCode(String code);
    
    boolean existsByCodeIgnoreCaseAndIsDeletedFalse(String code);
}


package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.ServiceTypeLangEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceTypeLangRepository extends JpaRepository<ServiceTypeLangEntity, UUID>,
        JpaSpecificationExecutor<ServiceTypeLangEntity> {
    
    List<ServiceTypeLangEntity> findByServiceTypeId(UUID serviceTypeId);
    
    Optional<ServiceTypeLangEntity> findByServiceTypeIdAndLanguageCodeIgnoreCase(UUID serviceTypeId, String languageCode);
    
    List<ServiceTypeLangEntity> findByLanguageCode(String languageCode);
    
    List<ServiceTypeLangEntity> findByLanguageCodeAndIsActiveTrue(String languageCode);
}


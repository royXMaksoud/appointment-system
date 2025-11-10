package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.ServiceTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceTypeRepository extends 
        JpaRepository<ServiceTypeEntity, UUID>,
        JpaSpecificationExecutor<ServiceTypeEntity> {
    
    Optional<ServiceTypeEntity> findByCode(String code);
    
    boolean existsByCode(String code);
    
    // Find all root/general services (no parent)
    List<ServiceTypeEntity> findByParentServiceTypeIdIsNullAndIsActiveTrue();
    
    // Find all sub-services of a parent
    List<ServiceTypeEntity> findByParentServiceTypeIdAndIsActiveTrue(UUID parentId);
    
    @Query("SELECT st FROM ServiceTypeEntity st WHERE st.isActive = true")
    List<ServiceTypeEntity> findAllActive();
    
    boolean existsByNameIgnoreCaseAndIsDeletedFalse(String name);
}


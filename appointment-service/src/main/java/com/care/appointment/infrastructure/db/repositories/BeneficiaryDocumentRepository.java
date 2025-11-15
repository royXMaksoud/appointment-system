package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.BeneficiaryDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BeneficiaryDocumentRepository extends 
        JpaRepository<BeneficiaryDocumentEntity, UUID>,
        JpaSpecificationExecutor<BeneficiaryDocumentEntity> {
    
    List<BeneficiaryDocumentEntity> findByBeneficiaryIdAndIsDeletedFalse(UUID beneficiaryId);
    
    List<BeneficiaryDocumentEntity> findByBeneficiaryIdAndIsActiveTrueAndIsDeletedFalse(UUID beneficiaryId);
    
    List<BeneficiaryDocumentEntity> findByBeneficiaryIdAndDocumentTypeCodeValueId(UUID beneficiaryId, UUID documentTypeCodeValueId);
    
    long countByBeneficiaryId(UUID beneficiaryId);
}


package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.FamilyMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyMemberRepository extends 
        JpaRepository<FamilyMemberEntity, UUID>,
        JpaSpecificationExecutor<FamilyMemberEntity> {
    
    List<FamilyMemberEntity> findByBeneficiaryIdAndIsDeletedFalse(UUID beneficiaryId);
    
    List<FamilyMemberEntity> findByBeneficiaryIdAndIsActiveTrueAndIsDeletedFalse(UUID beneficiaryId);
    
    List<FamilyMemberEntity> findByBeneficiaryIdAndIsEmergencyContactTrue(UUID beneficiaryId);
    
    Optional<FamilyMemberEntity> findByNationalId(String nationalId);
    
    boolean existsByNationalId(String nationalId);
    
    long countByBeneficiaryId(UUID beneficiaryId);
}


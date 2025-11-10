package com.care.appointment.domain.ports.in.family;

import com.care.appointment.domain.model.FamilyMember;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FamilyMemberSearchPort {
    List<FamilyMember> findByBeneficiaryId(UUID beneficiaryId);
    
    List<FamilyMember> findActiveByBeneficiaryId(UUID beneficiaryId);
    
    List<FamilyMember> findEmergencyContactsByBeneficiaryId(UUID beneficiaryId);
    
    Optional<FamilyMember> findByNationalId(String nationalId);
    
    boolean existsByNationalId(String nationalId);
    
    long countByBeneficiaryId(UUID beneficiaryId);
}


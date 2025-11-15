package com.care.appointment.domain.ports.in.document;

import com.care.appointment.domain.model.BeneficiaryDocument;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeneficiaryDocumentSearchPort {
    List<BeneficiaryDocument> findByBeneficiaryId(UUID beneficiaryId);
    
    List<BeneficiaryDocument> findActiveByBeneficiaryId(UUID beneficiaryId);
    
    List<BeneficiaryDocument> findByBeneficiaryIdAndDocumentType(UUID beneficiaryId, UUID documentTypeCodeValueId);
    
    long countByBeneficiaryId(UUID beneficiaryId);
}


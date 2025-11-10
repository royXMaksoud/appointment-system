package com.care.appointment.domain.ports.in.document;

import com.care.appointment.domain.model.BeneficiaryDocument;
import java.util.Optional;
import java.util.UUID;

public interface BeneficiaryDocumentCrudPort {
    BeneficiaryDocument save(BeneficiaryDocument domain);
    BeneficiaryDocument update(BeneficiaryDocument domain);
    Optional<BeneficiaryDocument> findById(UUID id);
    void deleteById(UUID id);
}


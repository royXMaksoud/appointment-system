package com.care.appointment.domain.ports.in.beneficiary;

import com.care.appointment.domain.model.Beneficiary;

import java.util.Optional;
import java.util.UUID;

public interface LoadUseCase {
    Optional<Beneficiary> getBeneficiaryById(UUID beneficiaryId);
}


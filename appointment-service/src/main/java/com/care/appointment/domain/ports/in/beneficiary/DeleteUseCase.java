package com.care.appointment.domain.ports.in.beneficiary;

import java.util.UUID;

public interface DeleteUseCase {
    void deleteBeneficiary(UUID beneficiaryId);
}


package com.care.appointment.domain.ports.in.beneficiary;

import com.care.appointment.application.beneficiary.command.UpdateBeneficiaryCommand;
import com.care.appointment.domain.model.Beneficiary;

public interface UpdateUseCase {
    Beneficiary updateBeneficiary(UpdateBeneficiaryCommand command);
}


package com.care.appointment.domain.ports.in.beneficiary;

import com.care.appointment.application.beneficiary.command.CreateBeneficiaryCommand;
import com.care.appointment.domain.model.Beneficiary;

public interface SaveUseCase {
    Beneficiary saveBeneficiary(CreateBeneficiaryCommand command);
}


package com.care.appointment.domain.ports.in.servicetype;

import com.care.appointment.application.servicetype.command.UpdateServiceTypeCommand;
import com.care.appointment.domain.model.ServiceType;

public interface UpdateUseCase {
    ServiceType updateServiceType(UpdateServiceTypeCommand command);
}


package com.care.appointment.domain.ports.in.servicetype;

import com.care.appointment.application.servicetype.command.CreateServiceTypeCommand;
import com.care.appointment.domain.model.ServiceType;

public interface SaveUseCase {
    ServiceType saveServiceType(CreateServiceTypeCommand command);
}


package com.care.appointment.domain.ports.in.servicetypelanguage;

import com.care.appointment.application.servicetypelanguage.command.CreateServiceTypeLanguageCommand;
import com.care.appointment.domain.model.ServiceTypeLanguage;

public interface SaveUseCase {
    ServiceTypeLanguage saveServiceTypeLanguage(CreateServiceTypeLanguageCommand command);
}



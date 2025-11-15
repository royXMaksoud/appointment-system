package com.care.appointment.domain.ports.in.servicetypelanguage;

import com.care.appointment.application.servicetypelanguage.command.UpdateServiceTypeLanguageCommand;
import com.care.appointment.domain.model.ServiceTypeLanguage;

public interface UpdateUseCase {
    ServiceTypeLanguage updateServiceTypeLanguage(UpdateServiceTypeLanguageCommand command);
}



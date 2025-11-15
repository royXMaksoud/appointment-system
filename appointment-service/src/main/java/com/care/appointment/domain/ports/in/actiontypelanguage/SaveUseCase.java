package com.care.appointment.domain.ports.in.actiontypelanguage;

import com.care.appointment.application.actiontypelanguage.command.CreateActionTypeLanguageCommand;
import com.care.appointment.domain.model.ActionTypeLanguage;

public interface SaveUseCase {
    ActionTypeLanguage saveActionTypeLanguage(CreateActionTypeLanguageCommand command);
}



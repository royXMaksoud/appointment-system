package com.care.appointment.domain.ports.in.actiontypelanguage;

import com.care.appointment.application.actiontypelanguage.command.UpdateActionTypeLanguageCommand;
import com.care.appointment.domain.model.ActionTypeLanguage;

public interface UpdateUseCase {
    ActionTypeLanguage updateActionTypeLanguage(UpdateActionTypeLanguageCommand command);
}



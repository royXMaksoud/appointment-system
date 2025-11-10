package com.care.appointment.domain.ports.in.actiontype;

import com.care.appointment.application.actiontype.command.CreateActionTypeCommand;
import com.care.appointment.domain.model.ActionType;

public interface SaveUseCase {
    ActionType saveActionType(CreateActionTypeCommand command);
}


package com.care.appointment.domain.ports.in.actiontype;

import com.care.appointment.application.actiontype.command.UpdateActionTypeCommand;
import com.care.appointment.domain.model.ActionType;

public interface UpdateUseCase {
    ActionType updateActionType(UpdateActionTypeCommand command);
}


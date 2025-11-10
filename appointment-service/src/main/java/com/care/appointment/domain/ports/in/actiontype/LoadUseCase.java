package com.care.appointment.domain.ports.in.actiontype;

import com.care.appointment.domain.model.ActionType;

import java.util.Optional;
import java.util.UUID;

public interface LoadUseCase {
    Optional<ActionType> getActionTypeById(UUID id);
}


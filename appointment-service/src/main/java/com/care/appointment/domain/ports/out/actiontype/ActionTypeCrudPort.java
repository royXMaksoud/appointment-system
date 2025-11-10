package com.care.appointment.domain.ports.out.actiontype;

import com.care.appointment.domain.model.ActionType;

import java.util.Optional;
import java.util.UUID;

public interface ActionTypeCrudPort {
    ActionType save(ActionType entity);
    ActionType update(ActionType entity);
    Optional<ActionType> findById(UUID id);
    void deleteById(UUID id);
}


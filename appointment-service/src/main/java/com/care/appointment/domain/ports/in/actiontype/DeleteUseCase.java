package com.care.appointment.domain.ports.in.actiontype;

import java.util.UUID;

public interface DeleteUseCase {
    void deleteActionType(UUID id);
}


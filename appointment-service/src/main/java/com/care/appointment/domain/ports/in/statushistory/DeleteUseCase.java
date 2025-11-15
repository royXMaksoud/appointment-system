package com.care.appointment.domain.ports.in.statushistory;

import java.util.UUID;

public interface DeleteUseCase {
    void delete(UUID historyId);
}


package com.care.appointment.domain.ports.in.servicetype;

import java.util.UUID;

public interface DeleteUseCase {
    void deleteServiceType(UUID id);
}


package com.care.appointment.domain.ports.in.referral;

import java.util.UUID;

public interface DeleteUseCase {
    void delete(UUID referralId);
}


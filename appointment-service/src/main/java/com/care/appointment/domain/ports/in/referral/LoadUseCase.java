package com.care.appointment.domain.ports.in.referral;

import com.care.appointment.domain.model.AppointmentReferral;

import java.util.Optional;
import java.util.UUID;

public interface LoadUseCase {
    Optional<AppointmentReferral> getById(UUID referralId);
}


package com.care.appointment.domain.ports.in.referral;

import com.care.appointment.domain.model.AppointmentReferral;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentReferralCrudPort {
    AppointmentReferral save(AppointmentReferral domain);
    Optional<AppointmentReferral> findById(UUID id);
}


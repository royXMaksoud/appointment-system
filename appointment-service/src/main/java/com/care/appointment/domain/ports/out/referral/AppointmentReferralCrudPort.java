package com.care.appointment.domain.ports.out.referral;

import com.care.appointment.domain.model.AppointmentReferral;

import java.util.Optional;
import java.util.UUID;

public interface AppointmentReferralCrudPort {
    AppointmentReferral save(AppointmentReferral referral);
    AppointmentReferral update(AppointmentReferral referral);
    Optional<AppointmentReferral> findById(UUID referralId);
    void deleteById(UUID referralId);
}


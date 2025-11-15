package com.care.appointment.domain.ports.in.referral;

import com.care.appointment.domain.model.AppointmentReferral;

public interface SaveUseCase {
    AppointmentReferral save(AppointmentReferral referral);
}


package com.care.appointment.domain.ports.in.referral;

import com.care.appointment.domain.model.AppointmentReferral;

public interface UpdateUseCase {
    AppointmentReferral update(AppointmentReferral referral);
}


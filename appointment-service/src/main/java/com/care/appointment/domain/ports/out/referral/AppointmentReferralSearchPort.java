package com.care.appointment.domain.ports.out.referral;

import com.care.appointment.domain.model.AppointmentReferral;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentReferralSearchPort {
    Page<AppointmentReferral> search(FilterRequest filter, Pageable pageable);
}


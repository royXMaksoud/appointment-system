package com.care.appointment.domain.ports.in.referral;

import com.care.appointment.domain.model.AppointmentReferral;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Legacy placeholder retained for backwards compatibility. Prefer using the dedicated use case interfaces.
 */
@Deprecated
public interface AppointmentReferralCrudPort {
    AppointmentReferral save(AppointmentReferral domain);
    Optional<AppointmentReferral> findById(UUID id);
}


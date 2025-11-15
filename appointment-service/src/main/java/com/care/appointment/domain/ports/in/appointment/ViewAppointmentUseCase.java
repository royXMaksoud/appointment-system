package com.care.appointment.domain.ports.in.appointment;

import com.care.appointment.domain.model.Appointment;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ViewAppointmentUseCase {
    Optional<Appointment> getAppointmentById(UUID appointmentId);
    Optional<Appointment> getAppointmentByCode(String appointmentCode);
    Page<Appointment> getAllAppointments(FilterRequest filter, Pageable pageable);
    Page<Appointment> getAppointmentsByBeneficiary(UUID beneficiaryId, Pageable pageable);
}


package com.care.appointment.application.referral.service;

import com.care.appointment.application.referral.command.CreateAppointmentReferralCommand;
import com.care.appointment.application.referral.command.UpdateAppointmentReferralCommand;
import com.care.appointment.domain.model.AppointmentReferral;
import com.care.appointment.domain.ports.in.referral.*;
import com.care.appointment.domain.ports.out.referral.AppointmentReferralCrudPort;
import com.care.appointment.domain.ports.out.referral.AppointmentReferralSearchPort;
import com.sharedlib.core.filter.FilterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppointmentReferralService implements
        SaveUseCase,
        UpdateUseCase,
        LoadUseCase,
        DeleteUseCase,
        LoadAllUseCase {

    private final AppointmentReferralCrudPort referralCrudPort;
    private final AppointmentReferralSearchPort referralSearchPort;

    public AppointmentReferral createReferral(CreateAppointmentReferralCommand command) {
        log.info("Creating referral for appointment {}", command.getAppointmentId());

        AppointmentReferral referral = AppointmentReferral.builder()
                .appointmentId(command.getAppointmentId())
                .beneficiaryId(command.getBeneficiaryId())
                .referredToAppointmentId(command.getReferredToAppointmentId())
                .referredToServiceTypeId(command.getReferredToServiceTypeId())
                .referralType(command.getReferralType())
                .reason(command.getReason())
                .clinicalNotes(command.getClinicalNotes())
                .status(command.getStatus() != null ? command.getStatus() : "PENDING")
                .referralDate(command.getReferralDate() != null ? command.getReferralDate() : Instant.now())
                .referredAppointmentDate(command.getReferredAppointmentDate())
                .isUrgent(command.getIsUrgent() != null && command.getIsUrgent())
                .rejectionReason(command.getRejectionReason())
                .createdById(command.getCreatedById())
                .build();

        AppointmentReferral saved = referralCrudPort.save(referral);
        log.info("Referral created: {}", saved.getReferralId());
        return saved;
    }

    public AppointmentReferral updateReferral(UpdateAppointmentReferralCommand command) {
        log.info("Updating referral {}", command.getReferralId());

        AppointmentReferral existing = referralCrudPort.findById(command.getReferralId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment referral not found: " + command.getReferralId()));

        existing.setReferredToAppointmentId(command.getReferredToAppointmentId());
        existing.setReferredToServiceTypeId(command.getReferredToServiceTypeId());
        existing.setReferralType(command.getReferralType());
        existing.setReason(command.getReason());
        existing.setClinicalNotes(command.getClinicalNotes());
        if (command.getStatus() != null) {
            existing.setStatus(command.getStatus());
        }
        existing.setReferralDate(command.getReferralDate() != null ? command.getReferralDate() : existing.getReferralDate());
        existing.setReferredAppointmentDate(command.getReferredAppointmentDate());
        if (command.getIsUrgent() != null) {
            existing.setIsUrgent(command.getIsUrgent());
        }
        existing.setRejectionReason(command.getRejectionReason());
        existing.setUpdatedAt(Instant.now());
        existing.setUpdatedById(command.getUpdatedById());

        AppointmentReferral updated = referralCrudPort.update(existing);
        log.info("Referral updated: {}", updated.getReferralId());
        return updated;
    }

    @Override
    public AppointmentReferral save(AppointmentReferral referral) {
        return referralCrudPort.save(referral);
    }

    @Override
    public AppointmentReferral update(AppointmentReferral referral) {
        return referralCrudPort.update(referral);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppointmentReferral> getById(UUID referralId) {
        return referralCrudPort.findById(referralId);
    }

    @Override
    public void delete(UUID referralId) {
        log.info("Deleting referral {}", referralId);
        referralCrudPort.deleteById(referralId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentReferral> loadAll(FilterRequest filter, Pageable pageable) {
        return referralSearchPort.search(filter, pageable);
    }
}


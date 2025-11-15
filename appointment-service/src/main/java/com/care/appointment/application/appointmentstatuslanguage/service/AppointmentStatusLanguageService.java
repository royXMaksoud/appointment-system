package com.care.appointment.application.appointmentstatuslanguage.service;

import com.care.appointment.application.appointmentstatuslanguage.command.CreateAppointmentStatusLanguageCommand;
import com.care.appointment.application.appointmentstatuslanguage.command.UpdateAppointmentStatusLanguageCommand;
import com.care.appointment.domain.model.AppointmentStatusLanguage;
import com.care.appointment.domain.ports.in.appointmentstatuslanguage.*;
import com.care.appointment.domain.ports.out.appointmentstatuslanguage.AppointmentStatusLanguageCrudPort;
import com.care.appointment.domain.ports.out.appointmentstatuslanguage.AppointmentStatusLanguageSearchPort;
import com.sharedlib.core.filter.FilterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppointmentStatusLanguageService implements
        SaveUseCase,
        UpdateUseCase,
        LoadUseCase,
        DeleteUseCase,
        LoadAllUseCase {

    private final AppointmentStatusLanguageCrudPort crudPort;
    private final AppointmentStatusLanguageSearchPort searchPort;

    @Override
    public AppointmentStatusLanguage saveAppointmentStatusLanguage(CreateAppointmentStatusLanguageCommand command) {
        log.debug("Creating appointment status language for statusId={} lang={}", command.getAppointmentStatusId(), command.getLanguageCode());

        if (command.getAppointmentStatusId() == null) {
            throw new IllegalArgumentException("Appointment status id is required");
        }
        if (command.getLanguageCode() == null || command.getLanguageCode().isBlank()) {
            throw new IllegalArgumentException("Language code is required");
        }

        String normalizedLanguage = command.getLanguageCode().trim();
        searchPort.findByAppointmentStatusIdAndLanguageCode(command.getAppointmentStatusId(), normalizedLanguage)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Language already exists for this appointment status");
                });

        AppointmentStatusLanguage language = AppointmentStatusLanguage.builder()
                .appointmentStatusId(command.getAppointmentStatusId())
                .languageCode(normalizedLanguage)
                .name(command.getName() != null ? command.getName().trim() : null)
                .isActive(command.getIsActive() != null ? command.getIsActive() : Boolean.TRUE)
                .isDeleted(command.getIsDeleted() != null ? command.getIsDeleted() : Boolean.FALSE)
                .build();

        return crudPort.save(language);
    }

    @Override
    public AppointmentStatusLanguage updateAppointmentStatusLanguage(UpdateAppointmentStatusLanguageCommand command) {
        log.debug("Updating appointment status language {}", command.getAppointmentStatusLanguageId());

        AppointmentStatusLanguage existing = crudPort.findById(command.getAppointmentStatusLanguageId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment status language not found"));

        String requestedLanguage = command.getLanguageCode() != null ? command.getLanguageCode().trim() : null;
        if (requestedLanguage != null && !requestedLanguage.isBlank()) {
            if (!requestedLanguage.equalsIgnoreCase(existing.getLanguageCode())) {
                searchPort.findByAppointmentStatusIdAndLanguageCode(existing.getAppointmentStatusId(), requestedLanguage)
                        .ifPresent(other -> {
                            if (!other.getAppointmentStatusLanguageId().equals(existing.getAppointmentStatusLanguageId())) {
                                throw new IllegalArgumentException("Language already exists for this appointment status");
                            }
                        });
            }
            existing.setLanguageCode(requestedLanguage);
        }

        if (command.getAppointmentStatusId() != null && !command.getAppointmentStatusId().equals(existing.getAppointmentStatusId())) {
            throw new IllegalArgumentException("Appointment status association cannot be changed");
        }

        if (command.getName() != null) {
            existing.setName(command.getName().trim());
        }
        if (command.getIsActive() != null) {
            existing.setIsActive(command.getIsActive());
        }
        if (command.getIsDeleted() != null) {
            existing.setIsDeleted(command.getIsDeleted());
            if (command.getIsDeleted()) {
                existing.setIsActive(Boolean.FALSE);
            }
        }

        return crudPort.update(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppointmentStatusLanguage> getAppointmentStatusLanguageById(UUID id) {
        return crudPort.findById(id);
    }

    @Override
    public void deleteAppointmentStatusLanguage(UUID id) {
        AppointmentStatusLanguage existing = crudPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment status language not found"));
        existing.setIsDeleted(Boolean.TRUE);
        existing.setIsActive(Boolean.FALSE);
        crudPort.update(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentStatusLanguage> loadAllAppointmentStatusLanguages(FilterRequest filter, Pageable pageable) {
        return searchPort.search(filter, pageable);
    }
}



package com.care.appointment.application.appointmentstatus.service;

import com.care.appointment.application.appointmentstatus.command.CreateAppointmentStatusCommand;
import com.care.appointment.application.appointmentstatus.command.UpdateAppointmentStatusCommand;
import com.care.appointment.domain.model.AppointmentStatus;
import com.care.appointment.domain.ports.in.appointmentstatus.*;
import com.care.appointment.domain.ports.out.appointmentstatus.AppointmentStatusCrudPort;
import com.care.appointment.domain.ports.out.appointmentstatus.AppointmentStatusSearchPort;
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
public class AppointmentStatusService implements
        SaveUseCase,
        UpdateUseCase,
        LoadUseCase,
        DeleteUseCase,
        LoadAllUseCase {

    private final AppointmentStatusCrudPort crudPort;
    private final AppointmentStatusSearchPort searchPort;

    @Override
    public AppointmentStatus saveAppointmentStatus(CreateAppointmentStatusCommand command) {
        log.debug("Creating appointment status {}", command.getCode());
        if (command.getCode() == null || command.getCode().isBlank()) {
            throw new IllegalArgumentException("Status code is required");
        }
        if (command.getName() == null || command.getName().isBlank()) {
            throw new IllegalArgumentException("Status name is required");
        }
        if (crudPort.existsByCode(command.getCode().trim())) {
            throw new IllegalArgumentException("Status code already exists");
        }

        AppointmentStatus status = AppointmentStatus.builder()
                .code(command.getCode().trim())
                .name(command.getName().trim())
                .isActive(command.getIsActive() != null ? command.getIsActive() : Boolean.TRUE)
                .isDeleted(Boolean.FALSE)
                .build();

        return crudPort.save(status);
    }

    @Override
    public AppointmentStatus updateAppointmentStatus(UpdateAppointmentStatusCommand command) {
        log.debug("Updating appointment status {}", command.getAppointmentStatusId());
        AppointmentStatus existing = crudPort.findById(command.getAppointmentStatusId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment status not found"));

        if (command.getCode() != null && !command.getCode().isBlank()) {
            String newCode = command.getCode().trim();
            if (!newCode.equalsIgnoreCase(existing.getCode()) && crudPort.existsByCode(newCode)) {
                throw new IllegalArgumentException("Status code already exists");
            }
            existing.setCode(newCode);
        }

        if (command.getName() != null && !command.getName().isBlank()) {
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
    public Optional<AppointmentStatus> getAppointmentStatusById(UUID id) {
        return crudPort.findById(id);
    }

    @Override
    public void deleteAppointmentStatus(UUID id) {
        AppointmentStatus existing = crudPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment status not found"));
        existing.setIsDeleted(Boolean.TRUE);
        existing.setIsActive(Boolean.FALSE);
        crudPort.update(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentStatus> loadAllAppointmentStatuses(FilterRequest filter, Pageable pageable) {
        return searchPort.search(filter, pageable);
    }
}



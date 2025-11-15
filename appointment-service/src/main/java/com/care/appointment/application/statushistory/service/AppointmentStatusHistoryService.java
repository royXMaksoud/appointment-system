package com.care.appointment.application.statushistory.service;

import com.care.appointment.application.statushistory.command.CreateAppointmentStatusHistoryCommand;
import com.care.appointment.application.statushistory.command.UpdateAppointmentStatusHistoryCommand;
import com.care.appointment.domain.model.AppointmentStatusHistory;
import com.care.appointment.domain.ports.in.statushistory.*;
import com.care.appointment.domain.ports.out.statushistory.AppointmentStatusHistoryCrudPort;
import com.care.appointment.domain.ports.out.statushistory.AppointmentStatusHistorySearchPort;
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
public class AppointmentStatusHistoryService implements
        SaveUseCase,
        UpdateUseCase,
        LoadUseCase,
        DeleteUseCase,
        LoadAllUseCase {

    private final AppointmentStatusHistoryCrudPort historyCrudPort;
    private final AppointmentStatusHistorySearchPort historySearchPort;

    public AppointmentStatusHistory createHistory(CreateAppointmentStatusHistoryCommand command) {
        log.info("Creating history entry for appointment {}", command.getAppointmentId());

        AppointmentStatusHistory history = AppointmentStatusHistory.builder()
                .appointmentId(command.getAppointmentId())
                .appointmentStatusId(command.getAppointmentStatusId())
                .changedByUserId(command.getChangedByUserId())
                .reason(command.getReason())
                .changedAt(Instant.now())
                .build();

        AppointmentStatusHistory saved = historyCrudPort.save(history);
        log.info("History entry created: {}", saved.getHistoryId());
        return saved;
    }

    public AppointmentStatusHistory updateHistory(UpdateAppointmentStatusHistoryCommand command) {
        log.info("Updating history entry {}", command.getHistoryId());

        AppointmentStatusHistory existing = historyCrudPort.findById(command.getHistoryId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment status history not found: " + command.getHistoryId()));

        existing.setAppointmentStatusId(command.getAppointmentStatusId());
        existing.setChangedByUserId(command.getChangedByUserId());
        existing.setReason(command.getReason());

        AppointmentStatusHistory updated = historyCrudPort.update(existing);
        log.info("History entry updated: {}", updated.getHistoryId());
        return updated;
    }

    @Override
    public AppointmentStatusHistory save(AppointmentStatusHistory history) {
        return historyCrudPort.save(history);
    }

    @Override
    public AppointmentStatusHistory update(AppointmentStatusHistory history) {
        return historyCrudPort.update(history);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppointmentStatusHistory> getById(UUID historyId) {
        return historyCrudPort.findById(historyId);
    }

    @Override
    public void delete(UUID historyId) {
        log.info("Deleting history entry {}", historyId);
        historyCrudPort.deleteById(historyId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentStatusHistory> loadAll(FilterRequest filter, Pageable pageable) {
        return historySearchPort.search(filter, pageable);
    }
}


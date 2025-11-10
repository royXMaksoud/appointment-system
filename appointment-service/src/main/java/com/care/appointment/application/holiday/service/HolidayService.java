package com.care.appointment.application.holiday.service;

import com.care.appointment.application.holiday.command.CreateHolidayCommand;
import com.care.appointment.application.holiday.command.UpdateHolidayCommand;
import com.care.appointment.domain.model.Holiday;
import com.care.appointment.domain.ports.in.holiday.*;
import com.care.appointment.domain.ports.out.holiday.HolidayCrudPort;
import com.care.appointment.domain.ports.out.holiday.HolidaySearchPort;
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
public class HolidayService implements SaveUseCase, UpdateUseCase, LoadUseCase, DeleteUseCase, LoadAllUseCase {

    private final HolidayCrudPort holidayCrudPort;
    private final HolidaySearchPort holidaySearchPort;

    @Override
    public Holiday saveHoliday(CreateHolidayCommand command) {
        log.info("Creating new holiday for branch: {}, date: {}", command.getOrganizationBranchId(), command.getHolidayDate());

        // Validate no duplicate holiday for same branch and date
        if (holidaySearchPort.existsByOrganizationBranchIdAndHolidayDateAndIsDeletedFalse(
                command.getOrganizationBranchId(), command.getHolidayDate())) {
            throw new IllegalArgumentException("Holiday already exists for this branch and date");
        }

        Holiday holiday = Holiday.builder()
                .organizationBranchId(command.getOrganizationBranchId())
                .holidayDate(command.getHolidayDate())
                .name(command.getName())
                .reason(command.getReason())
                .isRecurringYearly(command.getIsRecurringYearly() != null ? command.getIsRecurringYearly() : false)
                .isActive(command.getIsActive() != null ? command.getIsActive() : true)
                .isDeleted(false)
                .createdById(command.getCreatedById())
                .build();

        Holiday saved = holidayCrudPort.save(holiday);
        log.info("Holiday created successfully with ID: {}", saved.getHolidayId());
        return saved;
    }

    @Override
    public Holiday updateHoliday(UpdateHolidayCommand command) {
        log.info("Updating holiday: {}", command.getHolidayId());

        Holiday existing = holidayCrudPort.findById(command.getHolidayId())
                .orElseThrow(() -> new IllegalArgumentException("Holiday not found with ID: " + command.getHolidayId()));

        if (existing.getIsDeleted()) {
            throw new IllegalArgumentException("Cannot update deleted holiday");
        }

        existing.setOrganizationBranchId(command.getOrganizationBranchId());
        existing.setHolidayDate(command.getHolidayDate());
        existing.setName(command.getName());
        existing.setReason(command.getReason());
        if (command.getIsRecurringYearly() != null) {
            existing.setIsRecurringYearly(command.getIsRecurringYearly());
        }
        if (command.getIsActive() != null) {
            existing.setIsActive(command.getIsActive());
        }

        Holiday updated = holidayCrudPort.update(existing);
        log.info("Holiday updated successfully: {}", updated.getHolidayId());
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Holiday> getHolidayById(UUID holidayId) {
        log.debug("Loading holiday by ID: {}", holidayId);
        return holidayCrudPort.findById(holidayId);
    }

    @Override
    public void deleteHoliday(UUID holidayId) {
        log.info("Deleting holiday: {}", holidayId);
        
        Holiday holiday = holidayCrudPort.findById(holidayId)
                .orElseThrow(() -> new IllegalArgumentException("Holiday not found with ID: " + holidayId));

        holiday.setIsDeleted(true);
        holiday.setIsActive(false);
        holidayCrudPort.update(holiday);
        
        log.info("Holiday deleted successfully (soft delete): {}", holidayId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Holiday> loadAll(FilterRequest filter, Pageable pageable) {
        log.debug("Loading all holidays with filter and pagination");
        return holidaySearchPort.search(filter, pageable);
    }
}


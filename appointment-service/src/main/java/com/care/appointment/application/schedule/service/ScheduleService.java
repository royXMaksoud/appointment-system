package com.care.appointment.application.schedule.service;

import com.care.appointment.application.schedule.command.CreateScheduleCommand;
import com.care.appointment.application.schedule.command.UpdateScheduleCommand;
import com.care.appointment.domain.model.Schedule;
import com.care.appointment.domain.ports.in.schedule.*;
import com.care.appointment.domain.ports.out.holiday.HolidaySearchPort;
import com.care.appointment.domain.ports.out.schedule.ScheduleCrudPort;
import com.care.appointment.domain.ports.out.schedule.ScheduleSearchPort;
import com.care.appointment.web.dto.admin.schedule.CreateScheduleBatchRequest;
import com.sharedlib.core.filter.FilterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ScheduleService implements SaveUseCase, UpdateUseCase, LoadUseCase, DeleteUseCase, LoadAllUseCase {

    private final ScheduleCrudPort scheduleCrudPort;
    private final ScheduleSearchPort scheduleSearchPort;
    private final HolidaySearchPort holidaySearchPort;

    @Override
    public Schedule saveSchedule(CreateScheduleCommand command) {
        log.info("Creating new schedule for branch: {}, day: {}", command.getOrganizationBranchId(), command.getDayOfWeek());

        // Validate no holiday exists for this branch and day of week
        if (holidaySearchPort.hasActiveHolidayOnDayOfWeek(command.getOrganizationBranchId(), command.getDayOfWeek())) {
            throw new IllegalArgumentException(
                    String.format("Cannot create schedule: %s (day %d) is marked as a holiday for this branch",
                            getDayName(command.getDayOfWeek()), command.getDayOfWeek()));
        }

        // Validate no duplicate schedule for same branch and day
        if (scheduleSearchPort.existsByOrganizationBranchIdAndDayOfWeekAndIsDeletedFalse(
                command.getOrganizationBranchId(), command.getDayOfWeek())) {
            throw new IllegalArgumentException("Schedule already exists for this branch and day of week");
        }

        // Validate start time is before end time
        if (!command.getStartTime().isBefore(command.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // Validate no overlapping time ranges for same branch and day
        if (scheduleSearchPort.hasOverlappingSchedule(
                command.getOrganizationBranchId(),
                command.getDayOfWeek(),
                command.getStartTime(),
                command.getEndTime(),
                null)) {
            throw new IllegalArgumentException(
                    String.format("Overlapping schedule found for day %d with time range %s-%s",
                            command.getDayOfWeek(), command.getStartTime(), command.getEndTime()));
        }

        Schedule schedule = Schedule.builder()
                .organizationBranchId(command.getOrganizationBranchId())
                .dayOfWeek(command.getDayOfWeek())
                .startTime(command.getStartTime())
                .endTime(command.getEndTime())
                .slotDurationMinutes(command.getSlotDurationMinutes())
                .maxCapacityPerSlot(command.getMaxCapacityPerSlot())
                .isActive(command.getIsActive() != null ? command.getIsActive() : true)
                .isDeleted(false)
                .createdById(command.getCreatedById())
                .build();

        Schedule saved = scheduleCrudPort.save(schedule);
        log.info("Schedule created successfully with ID: {}", saved.getScheduleId());
        return saved;
    }

    @Override
    public Schedule updateSchedule(UpdateScheduleCommand command) {
        log.info("Updating schedule: {}", command.getScheduleId());

        Schedule existing = scheduleCrudPort.findById(command.getScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + command.getScheduleId()));

        if (existing.getIsDeleted()) {
            throw new IllegalArgumentException("Cannot update deleted schedule");
        }

        // Validate no holiday exists for this branch and day of week
        if (holidaySearchPort.hasActiveHolidayOnDayOfWeek(command.getOrganizationBranchId(), command.getDayOfWeek())) {
            throw new IllegalArgumentException(
                    String.format("Cannot update schedule: %s (day %d) is marked as a holiday for this branch",
                            getDayName(command.getDayOfWeek()), command.getDayOfWeek()));
        }

        // Validate start time is before end time
        if (!command.getStartTime().isBefore(command.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        existing.setOrganizationBranchId(command.getOrganizationBranchId());
        existing.setDayOfWeek(command.getDayOfWeek());
        existing.setStartTime(command.getStartTime());
        existing.setEndTime(command.getEndTime());
        existing.setSlotDurationMinutes(command.getSlotDurationMinutes());
        existing.setMaxCapacityPerSlot(command.getMaxCapacityPerSlot());
        if (command.getIsActive() != null) {
            existing.setIsActive(command.getIsActive());
        }

        Schedule updated = scheduleCrudPort.update(existing);
        log.info("Schedule updated successfully: {}", updated.getScheduleId());
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Schedule> getScheduleById(UUID scheduleId) {
        log.debug("Loading schedule by ID: {}", scheduleId);
        return scheduleCrudPort.findById(scheduleId);
    }

    @Override
    public void deleteSchedule(UUID scheduleId) {
        log.info("Deleting schedule: {}", scheduleId);
        
        // Verify schedule exists before deletion
        scheduleCrudPort.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));

        // Hard delete - remove from database
        scheduleCrudPort.deleteById(scheduleId);
        
        log.info("Schedule deleted successfully from database: {}", scheduleId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Schedule> loadAll(FilterRequest filter, Pageable pageable) {
        log.debug("Loading all schedules with filter and pagination");
        return scheduleSearchPort.search(filter, pageable);
    }

    @Override
    public List<Schedule> saveSchedulesBatch(CreateScheduleBatchRequest request) {
        log.info("Creating batch schedules for branch: {}, days: {}", 
                request.getOrganizationBranchId(), request.getDaysOfWeek());

        List<Schedule> created = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Validate start time is before end time
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // Process each day
        for (Integer dayOfWeek : request.getDaysOfWeek()) {
            try {
                // Check if this day is a holiday
                if (holidaySearchPort.hasActiveHolidayOnDayOfWeek(request.getOrganizationBranchId(), dayOfWeek)) {
                    errors.add(String.format("Cannot create schedule: %s (day %d) is marked as a holiday for this branch",
                            getDayName(dayOfWeek), dayOfWeek));
                    continue;
                }

                // Check for duplicate (same branch + day)
                if (scheduleSearchPort.existsByOrganizationBranchIdAndDayOfWeekAndIsDeletedFalse(
                        request.getOrganizationBranchId(), dayOfWeek)) {
                    errors.add(String.format("Schedule already exists for %s (day %d)", 
                            getDayName(dayOfWeek), dayOfWeek));
                    continue;
                }

                // Check for overlapping time ranges
                if (scheduleSearchPort.hasOverlappingSchedule(
                        request.getOrganizationBranchId(), 
                        dayOfWeek, 
                        request.getStartTime(), 
                        request.getEndTime(), 
                        null)) {
                    errors.add(String.format("Overlapping schedule found for %s (day %d) with time range %s-%s", 
                            getDayName(dayOfWeek), dayOfWeek, 
                            request.getStartTime(), request.getEndTime()));
                    continue;
                }

                // Create schedule for this day
                Schedule schedule = Schedule.builder()
                        .organizationBranchId(request.getOrganizationBranchId())
                        .dayOfWeek(dayOfWeek)
                        .startTime(request.getStartTime())
                        .endTime(request.getEndTime())
                        .slotDurationMinutes(request.getSlotDurationMinutes())
                        .maxCapacityPerSlot(request.getMaxCapacityPerSlot())
                        .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                        .isDeleted(false)
                        .createdById(request.getCreatedById())
                        .build();

                Schedule saved = scheduleCrudPort.save(schedule);
                created.add(saved);
                log.info("Schedule created for day {}: {}", dayOfWeek, saved.getScheduleId());
            } catch (Exception e) {
                log.error("Failed to create schedule for day {}: {}", dayOfWeek, e.getMessage());
                errors.add(String.format("Failed to create schedule for %s (day %d): %s", 
                        getDayName(dayOfWeek), dayOfWeek, e.getMessage()));
            }
        }

        // If no schedules were created, throw error
        if (created.isEmpty()) {
            String errorMsg = errors.isEmpty() 
                    ? "Failed to create any schedules" 
                    : String.join("; ", errors);
            throw new IllegalArgumentException(errorMsg);
        }

        // If some schedules were created but some failed, log warnings
        if (!errors.isEmpty()) {
            log.warn("Some schedules failed to create: {}", String.join("; ", errors));
        }

        log.info("Batch creation completed: {} created, {} failed", created.size(), errors.size());
        return created;
    }

    private String getDayName(Integer dayOfWeek) {
        if (dayOfWeek == null) return "Unknown";
        return switch (dayOfWeek) {
            case 0 -> "Sunday";
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            case 4 -> "Thursday";
            case 5 -> "Friday";
            case 6 -> "Saturday";
            default -> "Unknown";
        };
    }
}


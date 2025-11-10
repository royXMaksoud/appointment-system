package com.care.appointment.domain.ports.out.schedule;

import com.care.appointment.domain.model.Schedule;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface ScheduleSearchPort {
    Page<Schedule> search(FilterRequest filter, Pageable pageable);
    boolean existsByOrganizationBranchIdAndDayOfWeekAndIsDeletedFalse(UUID branchId, Integer dayOfWeek);
    List<Schedule> findByOrganizationBranchIdAndDayOfWeekAndIsDeletedFalse(UUID branchId, Integer dayOfWeek);
    boolean hasOverlappingSchedule(UUID branchId, Integer dayOfWeek, LocalTime startTime, LocalTime endTime, UUID excludeScheduleId);
}


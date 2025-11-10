package com.care.appointment.domain.ports.out.holiday;

import com.care.appointment.domain.model.Holiday;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface HolidaySearchPort {
    Page<Holiday> search(FilterRequest filter, Pageable pageable);
    boolean existsByOrganizationBranchIdAndHolidayDateAndIsDeletedFalse(UUID branchId, LocalDate holidayDate);
    List<Holiday> findActiveHolidaysByOrganizationBranchId(UUID branchId);
    boolean hasActiveHolidayOnDayOfWeek(UUID branchId, Integer dayOfWeek);
}


package com.care.appointment.domain.ports.out.statushistory;

import com.care.appointment.domain.model.AppointmentStatusHistory;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentStatusHistorySearchPort {
    Page<AppointmentStatusHistory> search(FilterRequest filter, Pageable pageable);
}


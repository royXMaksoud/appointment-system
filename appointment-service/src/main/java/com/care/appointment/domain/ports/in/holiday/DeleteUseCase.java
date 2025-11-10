package com.care.appointment.domain.ports.in.holiday;

import java.util.UUID;

public interface DeleteUseCase {
    void deleteHoliday(UUID holidayId);
}


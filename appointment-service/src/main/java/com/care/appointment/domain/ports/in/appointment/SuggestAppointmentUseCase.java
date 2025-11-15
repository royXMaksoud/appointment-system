package com.care.appointment.domain.ports.in.appointment;

import com.care.appointment.application.appointment.query.NearestServiceCenterQuery;
import com.care.appointment.domain.model.NearestServiceCenterOption;

import java.util.List;

public interface SuggestAppointmentUseCase {

    List<NearestServiceCenterOption> findNearestByLocation(NearestServiceCenterQuery query);

    List<NearestServiceCenterOption> findNearestByAvailability(NearestServiceCenterQuery query);
}




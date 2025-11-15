package com.care.appointment.web.mapper;

import com.care.appointment.application.appointmentstatus.command.CreateAppointmentStatusCommand;
import com.care.appointment.application.appointmentstatus.command.UpdateAppointmentStatusCommand;
import com.care.appointment.domain.model.AppointmentStatus;
import com.care.appointment.web.dto.admin.appointmentstatus.AppointmentStatusResponse;
import com.care.appointment.web.dto.admin.appointmentstatus.CreateAppointmentStatusRequest;
import com.care.appointment.web.dto.admin.appointmentstatus.UpdateAppointmentStatusRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AppointmentStatusWebMapper {

    CreateAppointmentStatusCommand toCreateCommand(CreateAppointmentStatusRequest request);

    @Mapping(target = "appointmentStatusId", expression = "java(id)")
    UpdateAppointmentStatusCommand toUpdateCommand(UUID id, UpdateAppointmentStatusRequest request);

    AppointmentStatusResponse toResponse(AppointmentStatus status);
}



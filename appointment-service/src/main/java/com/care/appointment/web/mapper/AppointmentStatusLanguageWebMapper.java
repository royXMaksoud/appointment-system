package com.care.appointment.web.mapper;

import com.care.appointment.application.appointmentstatuslanguage.command.CreateAppointmentStatusLanguageCommand;
import com.care.appointment.application.appointmentstatuslanguage.command.UpdateAppointmentStatusLanguageCommand;
import com.care.appointment.domain.model.AppointmentStatusLanguage;
import com.care.appointment.web.dto.admin.appointmentstatus.AppointmentStatusLanguageResponse;
import com.care.appointment.web.dto.admin.appointmentstatus.CreateAppointmentStatusLanguageRequest;
import com.care.appointment.web.dto.admin.appointmentstatus.UpdateAppointmentStatusLanguageRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AppointmentStatusLanguageWebMapper {

    @Mapping(target = "isDeleted", constant = "false")
    CreateAppointmentStatusLanguageCommand toCreateCommand(CreateAppointmentStatusLanguageRequest request);

    @Mapping(target = "appointmentStatusLanguageId", expression = "java(id)")
    UpdateAppointmentStatusLanguageCommand toUpdateCommand(UUID id, UpdateAppointmentStatusLanguageRequest request);

    AppointmentStatusLanguageResponse toResponse(AppointmentStatusLanguage language);
}



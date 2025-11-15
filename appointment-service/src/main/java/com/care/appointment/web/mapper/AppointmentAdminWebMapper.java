package com.care.appointment.web.mapper;

import com.care.appointment.application.appointment.command.*;
import com.care.appointment.domain.model.Appointment;
import com.care.appointment.web.dto.admin.appointment.AppointmentDetailsResponse;
import com.care.appointment.web.dto.admin.appointment.CancelAppointmentRequest;
import com.care.appointment.web.dto.admin.appointment.CreateAppointmentRequest;
import com.care.appointment.web.dto.admin.appointment.TransferAppointmentRequest;
import com.care.appointment.web.dto.admin.appointment.UpdateAppointmentRequest;
import com.care.appointment.web.dto.admin.appointment.UpdateAppointmentStatusRequest;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppointmentAdminWebMapper {

    CreateAppointmentCommand toCreateCommand(CreateAppointmentRequest request);

    @Mapping(target = "appointmentId", source = "appointmentId")
    UpdateAppointmentCommand toUpdateCommand(UUID appointmentId, UpdateAppointmentRequest request);

    @Mapping(target = "appointmentId", source = "appointmentId")
    UpdateAppointmentStatusCommand toUpdateStatusCommand(UUID appointmentId, UpdateAppointmentStatusRequest request);

    @Mapping(target = "appointmentId", source = "appointmentId")
    CancelAppointmentCommand toCancelCommand(UUID appointmentId, CancelAppointmentRequest request);

    @Mapping(target = "appointmentId", source = "appointmentId")
    TransferAppointmentCommand toTransferCommand(UUID appointmentId, TransferAppointmentRequest request);

    AppointmentDetailsResponse toDetailsResponse(Appointment domain);
}


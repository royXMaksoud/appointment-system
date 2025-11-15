package com.care.appointment.web.mapper;

import com.care.appointment.application.statushistory.command.CreateAppointmentStatusHistoryCommand;
import com.care.appointment.application.statushistory.command.UpdateAppointmentStatusHistoryCommand;
import com.care.appointment.domain.model.AppointmentStatusHistory;
import com.care.appointment.web.dto.admin.appointment.history.AppointmentStatusHistoryResponse;
import com.care.appointment.web.dto.admin.appointment.history.CreateAppointmentStatusHistoryRequest;
import com.care.appointment.web.dto.admin.appointment.history.UpdateAppointmentStatusHistoryRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AppointmentStatusHistoryWebMapper {

    AppointmentStatusHistoryResponse toResponse(AppointmentStatusHistory history);

    CreateAppointmentStatusHistoryCommand toCreateCommand(CreateAppointmentStatusHistoryRequest request);

    @Mapping(target = "historyId", source = "historyId")
    UpdateAppointmentStatusHistoryCommand toUpdateCommand(UUID historyId, UpdateAppointmentStatusHistoryRequest request);
}


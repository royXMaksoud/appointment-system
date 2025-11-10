package com.care.appointment.web.mapper;

import com.care.appointment.application.holiday.command.CreateHolidayCommand;
import com.care.appointment.application.holiday.command.UpdateHolidayCommand;
import com.care.appointment.domain.model.Holiday;
import com.care.appointment.web.dto.admin.holiday.CreateHolidayRequest;
import com.care.appointment.web.dto.admin.holiday.HolidayResponse;
import com.care.appointment.web.dto.admin.holiday.UpdateHolidayRequest;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HolidayWebMapper {

    CreateHolidayCommand toCreateCommand(CreateHolidayRequest request);

    @Mapping(target = "holidayId", source = "holidayId")
    UpdateHolidayCommand toUpdateCommand(UUID holidayId, UpdateHolidayRequest request);

    HolidayResponse toResponse(Holiday domain);
}


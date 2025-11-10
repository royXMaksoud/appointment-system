package com.care.appointment.web.mapper;

import com.care.appointment.application.schedule.command.CreateScheduleCommand;
import com.care.appointment.application.schedule.command.UpdateScheduleCommand;
import com.care.appointment.domain.model.Schedule;
import com.care.appointment.web.dto.admin.schedule.CreateScheduleRequest;
import com.care.appointment.web.dto.admin.schedule.ScheduleResponse;
import com.care.appointment.web.dto.admin.schedule.UpdateScheduleRequest;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScheduleWebMapper {

    CreateScheduleCommand toCreateCommand(CreateScheduleRequest request);

    @Mapping(target = "scheduleId", source = "scheduleId")
    UpdateScheduleCommand toUpdateCommand(UUID scheduleId, UpdateScheduleRequest request);

    @Mapping(target = "dayName", expression = "java(getDayName(domain.getDayOfWeek()))")
    ScheduleResponse toResponse(Schedule domain);

    default String getDayName(Integer dayOfWeek) {
        if (dayOfWeek == null) return null;
        return switch (dayOfWeek) {
            case 0 -> "Sunday";
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            case 4 -> "Thursday";
            case 5 -> "Friday";
            case 6 -> "Saturday";
            default -> "Unknown";
        };
    }
}


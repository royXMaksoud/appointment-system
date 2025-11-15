package com.care.appointment.web.mapper;

import com.care.appointment.application.servicetypelanguage.command.CreateServiceTypeLanguageCommand;
import com.care.appointment.application.servicetypelanguage.command.UpdateServiceTypeLanguageCommand;
import com.care.appointment.domain.model.ServiceTypeLanguage;
import com.care.appointment.web.dto.admin.servicetype.CreateServiceTypeLanguageRequest;
import com.care.appointment.web.dto.admin.servicetype.ServiceTypeLanguageResponse;
import com.care.appointment.web.dto.admin.servicetype.UpdateServiceTypeLanguageRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ServiceTypeLanguageWebMapper {

    @Mapping(target = "isDeleted", constant = "false")
    CreateServiceTypeLanguageCommand toCreateCommand(CreateServiceTypeLanguageRequest request);

    @Mapping(target = "serviceTypeLanguageId", expression = "java(id)")
    UpdateServiceTypeLanguageCommand toUpdateCommand(UUID id, UpdateServiceTypeLanguageRequest request);

    ServiceTypeLanguageResponse toResponse(ServiceTypeLanguage language);
}



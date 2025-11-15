package com.care.appointment.web.mapper;

import com.care.appointment.application.actiontypelanguage.command.CreateActionTypeLanguageCommand;
import com.care.appointment.application.actiontypelanguage.command.UpdateActionTypeLanguageCommand;
import com.care.appointment.domain.model.ActionTypeLanguage;
import com.care.appointment.web.dto.admin.actiontype.ActionTypeLanguageResponse;
import com.care.appointment.web.dto.admin.actiontype.CreateActionTypeLanguageRequest;
import com.care.appointment.web.dto.admin.actiontype.UpdateActionTypeLanguageRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActionTypeLanguageWebMapper {

    CreateActionTypeLanguageCommand toCreateCommand(CreateActionTypeLanguageRequest request);

    @Mapping(target = "actionTypeLanguageId", expression = "java(id)")
    UpdateActionTypeLanguageCommand toUpdateCommand(java.util.UUID id, UpdateActionTypeLanguageRequest request);

    ActionTypeLanguageResponse toResponse(ActionTypeLanguage language);
}



package com.care.appointment.web.mapper;

import com.care.appointment.application.actiontype.command.CreateActionTypeCommand;
import com.care.appointment.application.actiontype.command.UpdateActionTypeCommand;
import com.care.appointment.domain.model.ActionType;
import com.care.appointment.web.dto.admin.actiontype.CreateActionTypeRequest;
import com.care.appointment.web.dto.admin.actiontype.ActionTypeResponse;
import com.care.appointment.web.dto.admin.actiontype.UpdateActionTypeRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ActionTypeWebMapper {
    
    public CreateActionTypeCommand toCreateCommand(CreateActionTypeRequest request) {
        return CreateActionTypeCommand.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .isActive(request.getIsActive())
                .requiresTransfer(request.getRequiresTransfer())
                .completesAppointment(request.getCompletesAppointment())
                .color(request.getColor())
                .displayOrder(request.getDisplayOrder())
                .build();
    }
    
    public UpdateActionTypeCommand toUpdateCommand(UUID id, UpdateActionTypeRequest request) {
        return UpdateActionTypeCommand.builder()
                .actionTypeId(id)
                .name(request.getName())
                .description(request.getDescription())
                .isActive(request.getIsActive())
                .requiresTransfer(request.getRequiresTransfer())
                .completesAppointment(request.getCompletesAppointment())
                .color(request.getColor())
                .displayOrder(request.getDisplayOrder())
                .build();
    }
    
    public ActionTypeResponse toResponse(ActionType domain) {
        return ActionTypeResponse.builder()
                .actionTypeId(domain.getActionTypeId())
                .name(domain.getName())
                .code(domain.getCode())
                .description(domain.getDescription())
                .isActive(domain.getIsActive())
                .isDeleted(domain.getIsDeleted())
                .requiresTransfer(domain.getRequiresTransfer())
                .completesAppointment(domain.getCompletesAppointment())
                .color(domain.getColor())
                .displayOrder(domain.getDisplayOrder())
                .createdById(domain.getCreatedById())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .rowVersion(domain.getRowVersion())
                .build();
    }
    
    public List<ActionTypeResponse> toResponseList(List<ActionType> list) {
        return list.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}


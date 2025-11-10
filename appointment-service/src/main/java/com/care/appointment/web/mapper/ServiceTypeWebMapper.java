package com.care.appointment.web.mapper;

import com.care.appointment.application.servicetype.command.CreateServiceTypeCommand;
import com.care.appointment.application.servicetype.command.UpdateServiceTypeCommand;
import com.care.appointment.domain.model.ServiceType;
import com.care.appointment.web.dto.admin.servicetype.CreateServiceTypeRequest;
import com.care.appointment.web.dto.admin.servicetype.ServiceTypeResponse;
import com.care.appointment.web.dto.admin.servicetype.UpdateServiceTypeRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ServiceTypeWebMapper {
    
    public CreateServiceTypeCommand toCreateCommand(CreateServiceTypeRequest request) {
        return CreateServiceTypeCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .parentId(request.getParentId())
                .isActive(request.getIsActive())
                .isLeaf(request.getIsLeaf())
                .code(request.getCode())
                .displayOrder(request.getDisplayOrder())
                .build();
    }
    
    public UpdateServiceTypeCommand toUpdateCommand(UUID id, UpdateServiceTypeRequest request) {
        return UpdateServiceTypeCommand.builder()
                .serviceTypeId(id)
                .name(request.getName())
                .description(request.getDescription())
                .parentId(request.getParentId())
                .isActive(request.getIsActive())
                .isLeaf(request.getIsLeaf())
                .code(request.getCode())
                .displayOrder(request.getDisplayOrder())
                .build();
    }
    
    public ServiceTypeResponse toResponse(ServiceType domain) {
        return ServiceTypeResponse.builder()
                .serviceTypeId(domain.getServiceTypeId())
                .name(domain.getName())
                .description(domain.getDescription())
                .parentId(domain.getParentId())
                .isActive(domain.getIsActive())
                .isDeleted(domain.getIsDeleted())
                .isLeaf(domain.getIsLeaf())
                .code(domain.getCode())
                .displayOrder(domain.getDisplayOrder())
                .createdById(domain.getCreatedById())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .rowVersion(domain.getRowVersion())
                .build();
    }
    
    public List<ServiceTypeResponse> toResponseList(List<ServiceType> list) {
        return list.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}


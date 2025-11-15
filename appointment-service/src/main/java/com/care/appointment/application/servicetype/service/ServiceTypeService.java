package com.care.appointment.application.servicetype.service;

import com.care.appointment.application.servicetype.command.CreateServiceTypeCommand;
import com.care.appointment.application.servicetype.command.UpdateServiceTypeCommand;
import com.care.appointment.domain.model.ServiceType;
import com.care.appointment.domain.ports.in.servicetype.*;
import com.care.appointment.domain.ports.out.servicetype.ServiceTypeCrudPort;
import com.care.appointment.domain.ports.out.servicetype.ServiceTypeSearchPort;
import com.sharedlib.core.filter.FilterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServiceTypeService implements SaveUseCase, UpdateUseCase, LoadUseCase, DeleteUseCase, LoadAllUseCase {
    
    private final ServiceTypeCrudPort crudPort;
    private final ServiceTypeSearchPort searchPort;
    
    @Override
    public ServiceType saveServiceType(CreateServiceTypeCommand command) {
        log.debug("Creating new service type: {}", command.getName());
        
        // Validate uniqueness
        if (searchPort.existsActiveByNameIgnoreCase(command.getName())) {
            throw new IllegalArgumentException("Service type with name '" + command.getName() + "' already exists");
        }
        
        ServiceType serviceType = ServiceType.builder()
                .name(command.getName())
                .description(command.getDescription())
                .parentId(command.getParentId())
                .isActive(command.getIsActive() != null ? command.getIsActive() : true)
                .isDeleted(false)
                .isLeaf(command.getIsLeaf() != null ? command.getIsLeaf() : true)
                .code(command.getCode())
                .displayOrder(command.getDisplayOrder())
                .build();
        
        return crudPort.save(serviceType);
    }
    
    @Override
    public ServiceType updateServiceType(UpdateServiceTypeCommand command) {
        log.debug("Updating service type: {}", command.getServiceTypeId());
        
        ServiceType existing = crudPort.findById(command.getServiceTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Service type not found: " + command.getServiceTypeId()));
        
        // Update fields
        existing.setName(command.getName());
        existing.setDescription(command.getDescription());
        existing.setParentId(command.getParentId());
        if (command.getIsActive() != null) {
            existing.setIsActive(command.getIsActive());
        }
        if (command.getIsLeaf() != null) {
            existing.setIsLeaf(command.getIsLeaf());
        }
        existing.setCode(command.getCode());
        existing.setDisplayOrder(command.getDisplayOrder());
        
        return crudPort.update(existing);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceType> getServiceTypeById(UUID id) {
        log.debug("Loading service type: {}", id);
        return crudPort.findById(id);
    }
    
    @Override
    public void deleteServiceType(UUID id) {
        log.debug("Deleting service type: {}", id);
        
        ServiceType existing = crudPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service type not found: " + id));

        // Perform hard delete
        crudPort.deleteById(existing.getServiceTypeId());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ServiceType> loadAll(FilterRequest filter, Pageable pageable) {
        log.debug("Loading all service types with filter: {}", filter);
        return searchPort.search(filter, pageable);
    }
}


package com.care.appointment.application.actiontype.service;

import com.care.appointment.application.actiontype.command.CreateActionTypeCommand;
import com.care.appointment.application.actiontype.command.UpdateActionTypeCommand;
import com.care.appointment.domain.model.ActionType;
import com.care.appointment.domain.ports.in.actiontype.*;
import com.care.appointment.domain.ports.out.actiontype.ActionTypeCrudPort;
import com.care.appointment.domain.ports.out.actiontype.ActionTypeSearchPort;
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
public class ActionTypeService implements SaveUseCase, UpdateUseCase, LoadUseCase, DeleteUseCase, LoadAllUseCase {
    
    private final ActionTypeCrudPort crudPort;
    private final ActionTypeSearchPort searchPort;
    
    @Override
    public ActionType saveActionType(CreateActionTypeCommand command) {
        log.debug("Creating new action type: {}", command.getName());
        
        // Validate uniqueness
        if (searchPort.existsActiveByCodeIgnoreCase(command.getCode())) {
            throw new IllegalArgumentException("Action type with code '" + command.getCode() + "' already exists");
        }
        
        ActionType actionType = ActionType.builder()
                .name(command.getName())
                .code(command.getCode())
                .description(command.getDescription())
                .isActive(command.getIsActive() != null ? command.getIsActive() : true)
                .isDeleted(false)
                .requiresTransfer(command.getRequiresTransfer() != null ? command.getRequiresTransfer() : false)
                .completesAppointment(command.getCompletesAppointment() != null ? command.getCompletesAppointment() : false)
                .color(command.getColor())
                .displayOrder(command.getDisplayOrder())
                .build();
        
        return crudPort.save(actionType);
    }
    
    @Override
    public ActionType updateActionType(UpdateActionTypeCommand command) {
        log.debug("Updating action type: {}", command.getActionTypeId());
        
        ActionType existing = crudPort.findById(command.getActionTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Action type not found: " + command.getActionTypeId()));
        
        // Update fields
        existing.setName(command.getName());
        existing.setDescription(command.getDescription());
        if (command.getIsActive() != null) {
            existing.setIsActive(command.getIsActive());
        }
        if (command.getRequiresTransfer() != null) {
            existing.setRequiresTransfer(command.getRequiresTransfer());
        }
        if (command.getCompletesAppointment() != null) {
            existing.setCompletesAppointment(command.getCompletesAppointment());
        }
        existing.setColor(command.getColor());
        existing.setDisplayOrder(command.getDisplayOrder());
        
        return crudPort.update(existing);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ActionType> getActionTypeById(UUID id) {
        log.debug("Loading action type: {}", id);
        return crudPort.findById(id);
    }
    
    @Override
    public void deleteActionType(UUID id) {
        log.debug("Deleting action type: {}", id);
        
        ActionType existing = crudPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Action type not found: " + id));
        
        existing.setIsDeleted(true);
        existing.setIsActive(false);
        crudPort.update(existing);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ActionType> loadAll(FilterRequest filter, Pageable pageable) {
        log.debug("Loading all action types with filter: {}", filter);
        return searchPort.search(filter, pageable);
    }
}


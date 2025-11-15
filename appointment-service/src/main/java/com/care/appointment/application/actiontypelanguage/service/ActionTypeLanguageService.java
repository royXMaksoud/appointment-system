package com.care.appointment.application.actiontypelanguage.service;

import com.care.appointment.application.actiontypelanguage.command.CreateActionTypeLanguageCommand;
import com.care.appointment.application.actiontypelanguage.command.UpdateActionTypeLanguageCommand;
import com.care.appointment.domain.model.ActionTypeLanguage;
import com.care.appointment.domain.ports.in.actiontypelanguage.*;
import com.care.appointment.domain.ports.out.actiontypelanguage.ActionTypeLanguageCrudPort;
import com.care.appointment.domain.ports.out.actiontypelanguage.ActionTypeLanguageSearchPort;
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
public class ActionTypeLanguageService implements
        SaveUseCase,
        UpdateUseCase,
        LoadUseCase,
        DeleteUseCase,
        LoadAllUseCase {

    private final ActionTypeLanguageCrudPort crudPort;
    private final ActionTypeLanguageSearchPort searchPort;

    @Override
    public ActionTypeLanguage saveActionTypeLanguage(CreateActionTypeLanguageCommand command) {
        log.debug("Creating action type language for actionTypeId={}, language={}", command.getActionTypeId(), command.getLanguageCode());

        if (command.getLanguageCode() == null || command.getLanguageCode().isBlank()) {
            throw new IllegalArgumentException("Language code is required");
        }
        if (command.getActionTypeId() == null) {
            throw new IllegalArgumentException("Action type id is required");
        }

        searchPort.findByActionTypeIdAndLanguageCode(command.getActionTypeId(), command.getLanguageCode())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Language already exists for this action type");
                });

        String normalizedLanguage = command.getLanguageCode().trim();
        ActionTypeLanguage language = ActionTypeLanguage.builder()
                .actionTypeId(command.getActionTypeId())
                .languageCode(normalizedLanguage)
                .name(command.getName() != null ? command.getName().trim() : null)
                .isActive(command.getIsActive() != null ? command.getIsActive() : Boolean.TRUE)
                .isDeleted(command.getIsDeleted() != null ? command.getIsDeleted() : Boolean.FALSE)
                .build();

        return crudPort.save(language);
    }

    @Override
    public ActionTypeLanguage updateActionTypeLanguage(UpdateActionTypeLanguageCommand command) {
        log.debug("Updating action type language {}", command.getActionTypeLanguageId());

        ActionTypeLanguage existing = crudPort.findById(command.getActionTypeLanguageId())
                .orElseThrow(() -> new IllegalArgumentException("Action type language not found"));

        String requestedLanguage = command.getLanguageCode() != null ? command.getLanguageCode().trim() : null;
        if (requestedLanguage != null && !requestedLanguage.isBlank()) {
            if (!requestedLanguage.equalsIgnoreCase(existing.getLanguageCode())) {
                searchPort.findByActionTypeIdAndLanguageCode(existing.getActionTypeId(), requestedLanguage)
                        .ifPresent(other -> {
                            if (!other.getActionTypeLanguageId().equals(existing.getActionTypeLanguageId())) {
                                throw new IllegalArgumentException("Language already exists for this action type");
                            }
                        });
            }
            existing.setLanguageCode(requestedLanguage);
        }

        if (command.getActionTypeId() != null && !command.getActionTypeId().equals(existing.getActionTypeId())) {
            throw new IllegalArgumentException("Action type association cannot be changed");
        }

        if (command.getName() != null) {
            existing.setName(command.getName().trim());
        }
        if (command.getIsActive() != null) {
            existing.setIsActive(command.getIsActive());
        }
        if (command.getIsDeleted() != null) {
            existing.setIsDeleted(command.getIsDeleted());
            if (command.getIsDeleted()) {
                existing.setIsActive(Boolean.FALSE);
            }
        }

        return crudPort.update(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ActionTypeLanguage> getActionTypeLanguageById(UUID id) {
        return crudPort.findById(id);
    }

    @Override
    public void deleteActionTypeLanguage(UUID id) {
        ActionTypeLanguage existing = crudPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Action type language not found"));
        existing.setIsDeleted(Boolean.TRUE);
        existing.setIsActive(Boolean.FALSE);
        crudPort.update(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActionTypeLanguage> loadAllActionTypeLanguages(FilterRequest filter, Pageable pageable) {
        return searchPort.search(filter, pageable);
    }
}



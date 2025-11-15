package com.care.appointment.application.servicetypelanguage.service;

import com.care.appointment.application.servicetypelanguage.command.CreateServiceTypeLanguageCommand;
import com.care.appointment.application.servicetypelanguage.command.UpdateServiceTypeLanguageCommand;
import com.care.appointment.domain.model.ServiceTypeLanguage;
import com.care.appointment.domain.ports.in.servicetypelanguage.*;
import com.care.appointment.domain.ports.out.servicetypelanguage.ServiceTypeLanguageCrudPort;
import com.care.appointment.domain.ports.out.servicetypelanguage.ServiceTypeLanguageSearchPort;
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
public class ServiceTypeLanguageService implements
        SaveUseCase,
        UpdateUseCase,
        LoadUseCase,
        DeleteUseCase,
        LoadAllUseCase {

    private final ServiceTypeLanguageCrudPort crudPort;
    private final ServiceTypeLanguageSearchPort searchPort;

    @Override
    public ServiceTypeLanguage saveServiceTypeLanguage(CreateServiceTypeLanguageCommand command) {
        log.debug("Creating service type language for serviceTypeId={}, language={}", command.getServiceTypeId(), command.getLanguageCode());

        if (command.getServiceTypeId() == null) {
            throw new IllegalArgumentException("Service type id is required");
        }
        if (command.getLanguageCode() == null || command.getLanguageCode().isBlank()) {
            throw new IllegalArgumentException("Language code is required");
        }

        String normalizedLanguage = command.getLanguageCode().trim();
        searchPort.findByServiceTypeIdAndLanguageCode(command.getServiceTypeId(), normalizedLanguage)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Language already exists for this service type");
                });

        ServiceTypeLanguage language = ServiceTypeLanguage.builder()
                .serviceTypeId(command.getServiceTypeId())
                .languageCode(normalizedLanguage)
                .name(command.getName() != null ? command.getName().trim() : null)
                .description(command.getDescription() != null ? command.getDescription().trim() : null)
                .isActive(command.getIsActive() != null ? command.getIsActive() : Boolean.TRUE)
                .isDeleted(command.getIsDeleted() != null ? command.getIsDeleted() : Boolean.FALSE)
                .build();

        return crudPort.save(language);
    }

    @Override
    public ServiceTypeLanguage updateServiceTypeLanguage(UpdateServiceTypeLanguageCommand command) {
        log.debug("Updating service type language {}", command.getServiceTypeLanguageId());

        ServiceTypeLanguage existing = crudPort.findById(command.getServiceTypeLanguageId())
                .orElseThrow(() -> new IllegalArgumentException("Service type language not found"));

        String requestedLanguage = command.getLanguageCode() != null ? command.getLanguageCode().trim() : null;
        if (requestedLanguage != null && !requestedLanguage.isBlank()) {
            if (!requestedLanguage.equalsIgnoreCase(existing.getLanguageCode())) {
                searchPort.findByServiceTypeIdAndLanguageCode(existing.getServiceTypeId(), requestedLanguage)
                        .ifPresent(other -> {
                            if (!other.getServiceTypeLanguageId().equals(existing.getServiceTypeLanguageId())) {
                                throw new IllegalArgumentException("Language already exists for this service type");
                            }
                        });
            }
            existing.setLanguageCode(requestedLanguage);
        }

        if (command.getServiceTypeId() != null && !command.getServiceTypeId().equals(existing.getServiceTypeId())) {
            throw new IllegalArgumentException("Service type association cannot be changed");
        }

        if (command.getName() != null) {
            existing.setName(command.getName().trim());
        }
        if (command.getDescription() != null) {
            existing.setDescription(command.getDescription().trim());
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
    public Optional<ServiceTypeLanguage> getServiceTypeLanguageById(UUID id) {
        return crudPort.findById(id);
    }

    @Override
    public void deleteServiceTypeLanguage(UUID id) {
        ServiceTypeLanguage existing = crudPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service type language not found"));
        existing.setIsDeleted(Boolean.TRUE);
        existing.setIsActive(Boolean.FALSE);
        crudPort.update(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceTypeLanguage> loadAllServiceTypeLanguages(FilterRequest filter, Pageable pageable) {
        return searchPort.search(filter, pageable);
    }
}



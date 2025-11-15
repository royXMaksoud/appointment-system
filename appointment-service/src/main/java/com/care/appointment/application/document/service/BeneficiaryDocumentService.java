package com.care.appointment.application.document.service;

import com.care.appointment.application.document.command.CreateBeneficiaryDocumentCommand;
import com.care.appointment.application.document.command.UpdateBeneficiaryDocumentCommand;
import com.care.appointment.application.document.mapper.BeneficiaryDocumentDomainMapper;
import com.care.appointment.application.common.exception.NotFoundException;
import com.care.appointment.domain.model.BeneficiaryDocument;
import com.care.appointment.domain.ports.in.document.BeneficiaryDocumentCrudPort;
import com.care.appointment.domain.ports.in.document.BeneficiaryDocumentSearchPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing beneficiary documents
 * 
 * Handles metadata for uploaded documents. Actual file storage is handled externally.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BeneficiaryDocumentService {

    private final BeneficiaryDocumentCrudPort crudPort;
    private final BeneficiaryDocumentSearchPort searchPort;
    private final BeneficiaryDocumentDomainMapper domainMapper;

    public BeneficiaryDocument create(CreateBeneficiaryDocumentCommand command) {
        log.debug("Creating document for beneficiary: {}", command.getBeneficiaryId());

        BeneficiaryDocument domain = domainMapper.toDomain(command);
        BeneficiaryDocument saved = crudPort.save(domain);
        
        log.info("Created document: {} for beneficiary: {}", saved.getDocumentId(), command.getBeneficiaryId());
        return saved;
    }

    public BeneficiaryDocument update(UUID documentId, UpdateBeneficiaryDocumentCommand command) {
        log.debug("Updating document: {}", documentId);

        BeneficiaryDocument existing = crudPort.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found"));

        domainMapper.updateFromCommand(existing, command);
        BeneficiaryDocument updated = crudPort.update(existing);
        
        log.info("Updated document: {}", documentId);
        return updated;
    }

    @Transactional(readOnly = true)
    public BeneficiaryDocument getById(UUID documentId) {
        return crudPort.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found"));
    }

    @Transactional(readOnly = true)
    public List<BeneficiaryDocument> getByBeneficiaryId(UUID beneficiaryId) {
        log.debug("Getting documents for beneficiary: {}", beneficiaryId);
        return searchPort.findActiveByBeneficiaryId(beneficiaryId);
    }

    @Transactional(readOnly = true)
    public List<BeneficiaryDocument> getByBeneficiaryIdAndType(UUID beneficiaryId, UUID documentTypeId) {
        log.debug("Getting documents of type {} for beneficiary: {}", documentTypeId, beneficiaryId);
        return searchPort.findByBeneficiaryIdAndDocumentType(beneficiaryId, documentTypeId);
    }

    public void delete(UUID documentId) {
        log.debug("Deleting document: {}", documentId);

        BeneficiaryDocument existing = crudPort.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found"));

        existing.setIsDeleted(true);
        crudPort.update(existing);
        
        log.info("Deleted document: {}", documentId);
    }

    @Transactional(readOnly = true)
    public long countByBeneficiaryId(UUID beneficiaryId) {
        return searchPort.countByBeneficiaryId(beneficiaryId);
    }
}


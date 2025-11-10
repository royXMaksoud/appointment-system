package com.care.appointment.web.controller;

import com.care.appointment.application.document.command.CreateBeneficiaryDocumentCommand;
import com.care.appointment.application.document.command.UpdateBeneficiaryDocumentCommand;
import com.care.appointment.application.document.service.BeneficiaryDocumentService;
import com.care.appointment.domain.model.BeneficiaryDocument;
import com.care.appointment.web.dto.BeneficiaryDocumentDTO;
import com.care.appointment.web.mapper.BeneficiaryDocumentWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Beneficiary Document management
 * 
 * Provides CRUD operations for managing documents linked to beneficiaries.
 */
@RestController
@RequestMapping("/api/beneficiary-documents")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Beneficiary Documents", description = "Document management APIs")
public class BeneficiaryDocumentController {

    private final BeneficiaryDocumentService documentService;
    private final BeneficiaryDocumentWebMapper documentWebMapper;

    @PostMapping
    @Operation(summary = "Create a new document record")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Document created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<BeneficiaryDocumentDTO> create(@Valid @RequestBody BeneficiaryDocumentDTO request) {
        log.info("Creating document for beneficiary: {}", request.getBeneficiaryId());
        
        CreateBeneficiaryDocumentCommand command = documentWebMapper.toCreateCommand(request);
        BeneficiaryDocument created = documentService.create(command);
        BeneficiaryDocumentDTO response = documentWebMapper.toDTO(created);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing document record")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Document updated successfully"),
        @ApiResponse(responseCode = "404", description = "Document not found")
    })
    public ResponseEntity<BeneficiaryDocumentDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody BeneficiaryDocumentDTO request) {
        log.info("Updating document: {}", id);
        
        UpdateBeneficiaryDocumentCommand command = documentWebMapper.toUpdateCommand(id, request);
        BeneficiaryDocument updated = documentService.update(id, command);
        BeneficiaryDocumentDTO response = documentWebMapper.toDTO(updated);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get document by ID")
    public ResponseEntity<BeneficiaryDocumentDTO> getById(@PathVariable UUID id) {
        BeneficiaryDocument document = documentService.getById(id);
        BeneficiaryDocumentDTO response = documentWebMapper.toDTO(document);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/beneficiary/{beneficiaryId}")
    @Operation(summary = "Get all documents for a beneficiary")
    public ResponseEntity<List<BeneficiaryDocumentDTO>> getByBeneficiaryId(@PathVariable UUID beneficiaryId) {
        log.info("Getting documents for beneficiary: {}", beneficiaryId);
        
        List<BeneficiaryDocument> documents = documentService.getByBeneficiaryId(beneficiaryId);
        List<BeneficiaryDocumentDTO> response = documents.stream()
                .map(documentWebMapper::toDTO)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/beneficiary/{beneficiaryId}/type/{documentType}")
    @Operation(summary = "Get documents by type for a beneficiary")
    public ResponseEntity<List<BeneficiaryDocumentDTO>> getByBeneficiaryIdAndType(
            @PathVariable UUID beneficiaryId,
            @PathVariable String documentType) {
        log.info("Getting {} documents for beneficiary: {}", documentType, beneficiaryId);
        
        List<BeneficiaryDocument> documents = documentService.getByBeneficiaryIdAndType(beneficiaryId, documentType);
        List<BeneficiaryDocumentDTO> response = documents.stream()
                .map(documentWebMapper::toDTO)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete (soft delete) a document")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Document deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Document not found")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("Deleting document: {}", id);
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/beneficiary/{beneficiaryId}/count")
    @Operation(summary = "Get count of documents for a beneficiary")
    public ResponseEntity<Long> getCount(@PathVariable UUID beneficiaryId) {
        long count = documentService.countByBeneficiaryId(beneficiaryId);
        return ResponseEntity.ok(count);
    }
}


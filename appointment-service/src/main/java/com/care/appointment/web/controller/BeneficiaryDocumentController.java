package com.care.appointment.web.controller;

import com.care.appointment.application.document.command.CreateBeneficiaryDocumentCommand;
import com.care.appointment.application.document.command.UpdateBeneficiaryDocumentCommand;
import com.care.appointment.application.document.service.BeneficiaryDocumentService;
import com.care.appointment.domain.model.BeneficiaryDocument;
import com.care.appointment.infrastructure.storage.BeneficiaryDocumentStorageService;
import com.care.appointment.infrastructure.storage.BeneficiaryDocumentStorageService.StoredFile;
import com.care.appointment.web.dto.BeneficiaryDocumentDTO;
import com.care.appointment.web.mapper.BeneficiaryDocumentWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    private final BeneficiaryDocumentStorageService storageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a new beneficiary document")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Document created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<BeneficiaryDocumentDTO> create(
            @RequestParam("beneficiaryId") UUID beneficiaryId,
            @RequestParam("documentTypeId") UUID documentTypeId,
            @RequestParam(value = "documentTypeCode", required = false) String documentTypeCode,
            @RequestParam("documentName") String documentName,
            @RequestParam(value = "documentDescription", required = false) String documentDescription,
            @RequestPart("file") MultipartFile file,
            @RequestHeader(value = "User-Id", required = false) UUID userId,
            @RequestHeader(value = "X-User-Id", required = false) UUID xUserId
    ) {
        log.info("Uploading document for beneficiary: {}", beneficiaryId);
        StoredFile storedFile = null;
        try {
            storedFile = storageService.store(beneficiaryId, file);

            CreateBeneficiaryDocumentCommand command = CreateBeneficiaryDocumentCommand.builder()
                    .beneficiaryId(beneficiaryId)
                    .documentTypeCodeValueId(documentTypeId)
                    .documentTypeCode(normalize(documentTypeCode))
                    .documentName(documentName != null ? documentName.trim() : file.getOriginalFilename())
                    .legacyDocumentType(normalize(documentTypeCode))
                    .documentDescription(normalize(documentDescription))
                    .fileName(storedFile.fileName())
                    .fileExtension(storedFile.fileExtension())
                    .filePath(storedFile.relativePath())
                    .fileUrl(storedFile.relativePath())
                    .fileSizeBytes(storedFile.size())
                    .mimeType(storedFile.mimeType())
                    .createdById(resolveUserId(userId, xUserId).orElse(null))
                    .build();

            BeneficiaryDocument created = documentService.create(command);
            BeneficiaryDocumentDTO response = decorateDto(documentWebMapper.toDTO(created));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException ex) {
            if (storedFile != null) {
                storageService.deleteIfExists(storedFile.relativePath());
            }
            throw ex;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update an existing beneficiary document")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Document updated successfully"),
        @ApiResponse(responseCode = "404", description = "Document not found")
    })
    public ResponseEntity<BeneficiaryDocumentDTO> update(
            @PathVariable UUID id,
            @RequestParam("documentName") String documentName,
            @RequestParam("documentTypeId") UUID documentTypeId,
            @RequestParam(value = "documentTypeCode", required = false) String documentTypeCode,
            @RequestParam(value = "documentDescription", required = false) String documentDescription,
            @RequestPart(name = "file", required = false) MultipartFile file,
            @RequestHeader(value = "User-Id", required = false) UUID userId,
            @RequestHeader(value = "X-User-Id", required = false) UUID xUserId
    ) {
        log.info("Updating document: {}", id);

        BeneficiaryDocument existing = documentService.getById(id);
        StoredFile storedFile = null;
        String previousPath = existing.getFilePath();

        try {
            if (file != null && !file.isEmpty()) {
                storedFile = storageService.store(existing.getBeneficiaryId(), file);
            }

            UpdateBeneficiaryDocumentCommand command = UpdateBeneficiaryDocumentCommand.builder()
                    .documentId(id)
                    .documentName(documentName != null ? documentName.trim() : existing.getDocumentName())
                    .documentTypeCodeValueId(documentTypeId)
                    .documentTypeCode(normalize(documentTypeCode))
                    .legacyDocumentType(normalize(documentTypeCode))
                    .documentDescription(normalize(documentDescription))
                    .fileName(storedFile != null ? storedFile.fileName() : existing.getFileName())
                    .fileExtension(storedFile != null ? storedFile.fileExtension() : existing.getFileExtension())
                    .filePath(storedFile != null ? storedFile.relativePath() : existing.getFilePath())
                    .fileUrl(storedFile != null ? storedFile.relativePath() : existing.getFileUrl())
                    .fileSizeBytes(storedFile != null ? storedFile.size() : existing.getFileSizeBytes())
                    .mimeType(storedFile != null ? storedFile.mimeType() : existing.getMimeType())
                    .updatedById(resolveUserId(userId, xUserId).orElse(existing.getUpdatedById()))
                    .build();

            BeneficiaryDocument updated = documentService.update(id, command);
            if (storedFile != null && previousPath != null && !Objects.equals(previousPath, storedFile.relativePath())) {
                storageService.deleteIfExists(previousPath);
            }

            BeneficiaryDocumentDTO response = decorateDto(documentWebMapper.toDTO(updated));
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            if (storedFile != null) {
                storageService.deleteIfExists(storedFile.relativePath());
            }
            throw ex;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get document by ID")
    public ResponseEntity<BeneficiaryDocumentDTO> getById(@PathVariable UUID id) {
        BeneficiaryDocument document = documentService.getById(id);
        BeneficiaryDocumentDTO response = decorateDto(documentWebMapper.toDTO(document));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download beneficiary document file")
    public ResponseEntity<Resource> download(@PathVariable UUID id) {
        BeneficiaryDocument document = documentService.getById(id);
        Resource resource = storageService.loadAsResource(document.getFilePath());

        String filename = document.getFileName() != null ? document.getFileName() : ("document-" + id);
        String encodedFileName = encodeFileName(filename);

        MediaType mediaType = resolveMediaType(document.getMimeType());
        if (mediaType == null) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(java.util.Objects.requireNonNull(mediaType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .body(resource);
    }

    @GetMapping("/beneficiary/{beneficiaryId}")
    @Operation(summary = "Get all documents for a beneficiary")
    public ResponseEntity<List<BeneficiaryDocumentDTO>> getByBeneficiaryId(@PathVariable UUID beneficiaryId) {
        log.info("Getting documents for beneficiary: {}", beneficiaryId);
        
        List<BeneficiaryDocument> documents = documentService.getByBeneficiaryId(beneficiaryId);
        List<BeneficiaryDocumentDTO> response = documents.stream()
                .map(documentWebMapper::toDTO)
                .map(this::decorateDto)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/beneficiary/{beneficiaryId}/type/{documentTypeId}")
    @Operation(summary = "Get documents by type for a beneficiary")
    public ResponseEntity<List<BeneficiaryDocumentDTO>> getByBeneficiaryIdAndType(
            @PathVariable UUID beneficiaryId,
            @PathVariable UUID documentTypeId) {
        log.info("Getting documents of type {} for beneficiary: {}", documentTypeId, beneficiaryId);
        
        List<BeneficiaryDocument> documents = documentService.getByBeneficiaryIdAndType(beneficiaryId, documentTypeId);
        List<BeneficiaryDocumentDTO> response = documents.stream()
                .map(documentWebMapper::toDTO)
                .map(this::decorateDto)
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
        BeneficiaryDocument existing = documentService.getById(id);
        documentService.delete(id);
        storageService.deleteIfExists(existing.getFilePath());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/beneficiary/{beneficiaryId}/count")
    @Operation(summary = "Get count of documents for a beneficiary")
    public ResponseEntity<Long> getCount(@PathVariable UUID beneficiaryId) {
        long count = documentService.countByBeneficiaryId(beneficiaryId);
        return ResponseEntity.ok(count);
    }

    private BeneficiaryDocumentDTO decorateDto(BeneficiaryDocumentDTO dto) {
        if (dto == null) {
            return null;
        }
        dto.setDownloadUrl(buildDownloadUrl(dto.getDocumentId()));
        return dto;
    }

    private Optional<UUID> resolveUserId(UUID primaryHeader, UUID secondaryHeader) {
        if (primaryHeader != null) {
            return Optional.of(primaryHeader);
        }
        if (secondaryHeader != null) {
            return Optional.of(secondaryHeader);
        }
        Optional<UUID> fromSecurity = extractUserIdFromSecurityContext();
        if (fromSecurity.isPresent()) {
            return fromSecurity;
        }
        log.warn("Could not determine user id for document upload/update request. Falling back to null.");
        return Optional.empty();
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String buildDownloadUrl(UUID documentId) {
        if (documentId == null) {
            return null;
        }
        String downloadPath = "/appointment-service/api/beneficiary-documents/" + documentId + "/download";
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(downloadPath)
                .toUriString();
    }

    private Optional<UUID> extractUserIdFromSecurityContext() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                log.debug("No authenticated user found in SecurityContext");
                return Optional.empty();
            }

            Optional<UUID> fromAuthName = parseUuid(authentication.getName());
            if (fromAuthName.isPresent()) {
                return fromAuthName;
            }

            Object principal = authentication.getPrincipal();
            Optional<UUID> fromPrincipal = tryExtractUserIdFromObject(principal);
            if (fromPrincipal.isPresent()) {
                return fromPrincipal;
            }

            Object details = authentication.getDetails();
            Optional<UUID> fromDetails = tryExtractUserIdFromObject(details);
            if (fromDetails.isPresent()) {
                return fromDetails;
            }

            log.debug("Could not resolve user id from authentication principal/details: {}", principal != null ? principal.getClass().getName() : "null");
            return Optional.empty();
        } catch (Exception ex) {
            log.warn("Failed to extract user id from security context: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    private Optional<UUID> tryExtractUserIdFromObject(Object source) {
        if (source == null) {
            return Optional.empty();
        }

        if (source instanceof UUID uuid) {
            return Optional.of(uuid);
        }

        if (source instanceof String str && StringUtils.hasText(str)) {
            return parseUuid(str);
        }

        if (source instanceof Map<?, ?> map) {
            Object userIdValue = map.get("userId");
            if (userIdValue == null) {
                userIdValue = map.get("sub");
            }
            if (userIdValue == null) {
                return Optional.empty();
            }
            return tryExtractUserIdFromObject(userIdValue);
        }

        return Optional.empty();
    }

    private Optional<UUID> parseUuid(String value) {
        if (!StringUtils.hasText(value)) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException ex) {
            log.debug("Value is not a valid UUID: {}", value);
            return Optional.empty();
        }
    }

    private String encodeFileName(String filename) {
        try {
            return URLEncoder.encode(filename, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
        } catch (Exception ex) {
            return filename;
        }
    }

    private MediaType resolveMediaType(String mimeType) {
        try {
            return mimeType != null ? MediaType.parseMediaType(mimeType) : MediaType.APPLICATION_OCTET_STREAM;
        } catch (Exception ex) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}


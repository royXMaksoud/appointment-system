package com.care.appointment.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalBeneficiaryDocumentStorageService implements BeneficiaryDocumentStorageService {

    private final BeneficiaryDocumentStorageProperties properties;

    @jakarta.annotation.PostConstruct
    void init() {
        createDirectoriesIfNeeded(properties.getBaseDirectory().normalize());
    }

    @Override
    public StoredFile store(UUID beneficiaryId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new DocumentStorageException("Uploaded file is empty");
        }

        if (file.getSize() > properties.getMaxFileSizeBytes()) {
            throw new DocumentStorageException("File exceeds maximum allowed size");
        }

        String originalFilenameRaw = file.getOriginalFilename();
        String originalFilename = originalFilenameRaw != null ? StringUtils.cleanPath(originalFilenameRaw) : "";
        if (!StringUtils.hasText(originalFilename)) {
            originalFilename = "document";
        }
        String extension = extractAndValidateExtension(originalFilename);

        String safeFileName = UUID.randomUUID().toString() + (extension.isBlank() ? "" : "." + extension);
        Path beneficiaryDir = properties.getBaseDirectory().resolve(beneficiaryId.toString()).normalize();
        Path destination = beneficiaryDir.resolve(safeFileName).normalize();

        ensureWithinBaseDirectory(destination);
        createDirectoriesIfNeeded(beneficiaryDir);

        try {
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new DocumentStorageException("Failed to store beneficiary document", ex);
        }

        String mimeType = resolveMimeType(file, extension, destination);
        validateMimeType(mimeType);

        return new StoredFile(originalFilename, extension, properties.getBaseDirectory().relativize(destination).toString().replace('\\', '/'), file.getSize(), mimeType);
    }

    @Override
    public Resource loadAsResource(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            throw new DocumentStorageException("Document path is missing");
        }

        Path filePath = properties.getBaseDirectory().resolve(relativePath).normalize();
        ensureWithinBaseDirectory(filePath);

        try {
        Resource resource = new UrlResource(java.util.Objects.requireNonNull(filePath.toUri()));
            if (!resource.exists() || !resource.isReadable()) {
                throw new DocumentStorageException("Document file not found or not readable");
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new DocumentStorageException("Unable to read document file", ex);
        }
    }

    @Override
    public void deleteIfExists(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return;
        }

        Path filePath = properties.getBaseDirectory().resolve(relativePath).normalize();
        ensureWithinBaseDirectory(filePath);

        if (!Files.exists(filePath)) {
            return;
        }
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            log.warn("Failed to delete beneficiary document {}: {}", relativePath, ex.getMessage());
        }
    }

    private void ensureWithinBaseDirectory(Path path) {
        Path normalizedBase = properties.getBaseDirectory().normalize();
        if (!path.startsWith(normalizedBase)) {
            throw new DocumentStorageException("Invalid file path detected");
        }
    }

    private void createDirectoriesIfNeeded(Path beneficiaryDir) {
        try {
            Files.createDirectories(beneficiaryDir);
        } catch (IOException ex) {
            throw new DocumentStorageException("Unable to create directory for beneficiary documents", ex);
        }
    }

    private String extractAndValidateExtension(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        extension = extension != null ? extension.toLowerCase(Locale.ROOT) : "";

        Set<String> allowedExtensions = Set.copyOf(properties.getAllowedExtensions());
        if (!extension.isBlank() && !allowedExtensions.contains(extension)) {
            throw new DocumentStorageException("File type is not allowed");
        }

        return extension;
    }

    private String resolveMimeType(MultipartFile file, String extension, Path storedPath) {
        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType)) {
            try {
                contentType = Files.probeContentType(storedPath);
            } catch (IOException ex) {
                log.debug("Failed to probe MIME type via stored path: {}", ex.getMessage());
            }
        }

        if (!StringUtils.hasText(contentType)) {
            contentType = switch (extension) {
                case "jpg", "jpeg" -> "image/jpeg";
                case "png" -> "image/png";
                case "pdf" -> "application/pdf";
                case "doc" -> "application/msword";
                case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                default -> "application/octet-stream";
            };
        }

        return contentType;
    }

    private void validateMimeType(String mimeType) {
        if (!properties.getAllowedMimeTypes().isEmpty() && !properties.getAllowedMimeTypes().contains(mimeType)) {
            throw new DocumentStorageException("MIME type is not allowed");
        }
    }
}



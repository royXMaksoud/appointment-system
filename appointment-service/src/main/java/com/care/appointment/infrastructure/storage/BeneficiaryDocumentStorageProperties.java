package com.care.appointment.infrastructure.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "care.storage.beneficiary-documents")
public class BeneficiaryDocumentStorageProperties {

    /**
     * Base directory where beneficiary documents will be stored.
     */
    private Path baseDirectory = Paths.get("./beneficiariDocuments").toAbsolutePath().normalize();

    /**
     * Maximum allowed file size in bytes (default 10 MB).
     */
    private long maxFileSizeBytes = 10L * 1024 * 1024;

    /**
     * Allowed file extensions (lowercase, without dot).
     */
    private List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "pdf", "doc", "docx");

    /**
     * Allowed mime types for uploaded files.
     */
    private List<String> allowedMimeTypes = List.of(
            "image/jpeg",
            "image/png",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );
}



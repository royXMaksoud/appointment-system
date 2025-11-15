package com.care.appointment.infrastructure.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface BeneficiaryDocumentStorageService {

    StoredFile store(UUID beneficiaryId, MultipartFile file);

    Resource loadAsResource(String relativePath);

    void deleteIfExists(String relativePath);

    record StoredFile(String fileName,
                      String fileExtension,
                      String relativePath,
                      long size,
                      String mimeType) {}
}



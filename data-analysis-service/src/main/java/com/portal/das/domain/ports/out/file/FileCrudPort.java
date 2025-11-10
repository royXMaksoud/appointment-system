package com.portal.das.domain.ports.out.file;

import com.portal.das.domain.model.UploadedFile;
import com.sharedlib.core.domain.ports.out.CrudPort;

import java.util.UUID;

/**
 * Output port for file CRUD operations
 * This is the interface that infrastructure adapters must implement
 * CrudPort signature: <Domain, ID>
 */
public interface FileCrudPort extends CrudPort<UploadedFile, UUID> {
    // Inherits: save, load, delete
    // Additional file-specific methods can be added here if needed
}

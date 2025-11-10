package com.portal.das.domain.ports.out.file;

import com.portal.das.domain.model.UploadedFile;
import com.sharedlib.core.domain.ports.out.SearchPort;
import com.sharedlib.core.filter.FilterRequest;

/**
 * Output port for file search operations
 * This is the interface for querying and filtering files
 */
public interface FileSearchPort extends SearchPort<UploadedFile, FilterRequest> {
    // Inherits: search (with FilterRequest)
    // Additional search methods can be added here if needed
}

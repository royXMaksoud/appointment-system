package com.portal.das.domain.ports.out.dataset;

import com.portal.das.domain.model.Dataset;
import com.sharedlib.core.domain.ports.out.CrudPort;

import java.util.UUID;

/**
 * Output port for dataset CRUD operations
 * This is the interface that infrastructure adapters must implement
 */
public interface DatasetCrudPort extends CrudPort<Dataset, UUID> {
    // Inherits: save, load, delete
}



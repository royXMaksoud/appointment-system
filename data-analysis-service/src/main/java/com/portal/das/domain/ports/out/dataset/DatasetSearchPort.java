package com.portal.das.domain.ports.out.dataset;

import com.portal.das.domain.model.Dataset;
import com.sharedlib.core.domain.ports.out.SearchPort;
import com.sharedlib.core.filter.FilterRequest;

/**
 * Output port for dataset search operations
 */
public interface DatasetSearchPort extends SearchPort<Dataset, FilterRequest> {
    // Inherits: search (with FilterRequest)
}



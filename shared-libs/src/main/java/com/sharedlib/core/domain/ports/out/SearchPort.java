// SearchPort.java
package com.sharedlib.core.domain.ports.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchPort<D, F> {
    Page<D> search(F filter, Pageable pageable);
}

// SearchUseCase.java
package com.sharedlib.core.domain.ports.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchUseCase<F, R> {
    Page<R> search(F filter, Pageable pageable);
}

// GetByIdUseCase.java
package com.sharedlib.core.domain.ports.in;

public interface GetByIdUseCase<ID, R> {
    R getById(ID id);
}

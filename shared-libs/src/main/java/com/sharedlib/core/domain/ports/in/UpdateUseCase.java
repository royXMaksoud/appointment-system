// UpdateUseCase.java
package com.sharedlib.core.domain.ports.in;

public interface UpdateUseCase<ID, U, R> {
    R update(ID id, U command);
}

// DeleteUseCase.java
package com.sharedlib.core.domain.ports.in;

public interface DeleteUseCase<ID> {
    void delete(ID id);
}

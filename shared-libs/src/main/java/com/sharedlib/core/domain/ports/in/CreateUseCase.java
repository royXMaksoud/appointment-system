// CreateUseCase.java
package com.sharedlib.core.domain.ports.in;

public interface CreateUseCase<C, R> {
    R create(C command);
}

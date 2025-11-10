// CrudPort.java
package com.sharedlib.core.domain.ports.out;

import java.util.Optional;

public interface CrudPort<D, ID> {
    D save(D aggregate);
    Optional<D> load(ID id);
    void delete(ID id);
}

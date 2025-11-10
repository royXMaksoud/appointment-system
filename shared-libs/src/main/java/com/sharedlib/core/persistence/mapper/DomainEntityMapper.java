// DomainEntityMapper.java
package com.sharedlib.core.persistence.mapper;

public interface DomainEntityMapper<D, E> {
    E toEntity(D domain);
    D toDomain(E entity);
    void updateEntity(E target, D sourceDomain);
}

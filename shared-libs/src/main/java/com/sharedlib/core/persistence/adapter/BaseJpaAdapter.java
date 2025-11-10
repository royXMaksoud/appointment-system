// BaseJpaAdapter.java
package com.sharedlib.core.persistence.adapter;

import com.sharedlib.core.domain.ports.out.CrudPort;
import com.sharedlib.core.domain.ports.out.SearchPort;
import com.sharedlib.core.persistence.mapper.DomainEntityMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public abstract class BaseJpaAdapter<D, E, ID, F>
        implements CrudPort<D, ID>, SearchPort<D, F> {

    private final JpaRepository<E, ID> repository;
    private final JpaSpecificationExecutor<E> specRepository;
    private final DomainEntityMapper<D, E> mapper;

    protected BaseJpaAdapter(JpaRepository<E, ID> repository,
                             JpaSpecificationExecutor<E> specRepository,
                             DomainEntityMapper<D, E> mapper) {
        this.repository = repository;
        this.specRepository = specRepository;
        this.mapper = mapper;
    }

    @Override
    public D save(D aggregate) {
        E entity = mapper.toEntity(aggregate);
        E saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<D> load(ID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void delete(ID id) {
        repository.deleteById(id);
    }

    @Override
    public Page<D> search(F filter, Pageable pageable) {
        Specification<E> spec = buildSpecification(filter);
        Page<E> page = specRepository.findAll(spec, pageable);
        return page.map(mapper::toDomain);
    }

    /** **/
    protected abstract Specification<E> buildSpecification(F filter);
}

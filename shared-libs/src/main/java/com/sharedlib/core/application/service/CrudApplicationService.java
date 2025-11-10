// file: sharedlib/src/main/java/com/sharedlib/core/application/service/CrudApplicationService.java
package com.sharedlib.core.application.service;

import com.sharedlib.core.application.mapper.BaseMapper;
import com.sharedlib.core.domain.ports.in.*;
import com.sharedlib.core.domain.ports.out.CrudPort;
import com.sharedlib.core.domain.ports.out.SearchPort;
import com.sharedlib.core.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Generic CRUD Application Service.
 * - Maps commands to domain via BaseMapper
 * - Persists via ports (CrudPort, SearchPort)
 * - Provides extension hooks to inject domain-specific logic without repeating code.
 *
 * Type params:
 *  ID -> Identifier type (e.g., UUID)
 *  D  -> Domain aggregate/entity
 *  C  -> Create command
 *  U  -> Update command
 *  R  -> Response type (could be same as domain if you prefer)
 *  F  -> Filter request type
 */
public abstract class CrudApplicationService<ID, D, C, U, R, F>
        implements CreateUseCase<C, R>,
        UpdateUseCase<ID, U, R>,
        DeleteUseCase<ID>,
        GetByIdUseCase<ID, R>,
        SearchUseCase<F, R> {

    protected final CrudPort<D, ID> crudPort;
    protected final SearchPort<D, F> searchPort;
    protected final BaseMapper<D, C, U, R> mapper;

    protected CrudApplicationService(CrudPort<D, ID> crudPort,
                                     SearchPort<D, F> searchPort,
                                     BaseMapper<D, C, U, R> mapper) {
        this.crudPort = crudPort;
        this.searchPort = searchPort;
        this.mapper = mapper;
    }

    // ----------------------
    // Extension Hooks (OVERRIDE where needed)
    // ----------------------

    /**
     * Hook runs right after mapping the create command to domain and BEFORE persisting.
     * Good place for: validation, audit enrichment, calling external services.
     */
    protected D beforeCreate(D domain) { return domain; }

    /**
     * Hook runs right after loading current domain and applying the update command,
     * BUT BEFORE persisting.
     */
    protected D beforeUpdate(D current, U cmd) { return current; }

    /**
     * Hook runs AFTER saving the entity (create or update).
     * Good place for: notifications, domain events, side effects.
     */
    protected void afterSave(D saved) { /* no-op by default */ }

    /**
     * Customize how NotFound should be thrown if you need i18n or custom messages.
     */
    protected NotFoundException notFound(ID id) {
        return new NotFoundException("Resource not found: " + id);
    }

    // ----------------------
    // Template Methods
    // ----------------------

    @Override
    public R create(C cmd) {
        D domain = mapper.fromCreate(cmd);
        domain = beforeCreate(domain);             // <--- hook
        D saved = crudPort.save(domain);
        afterSave(saved);                          // <--- hook
        return mapper.toResponse(saved);
    }

    @Override
    public R update(ID id, U cmd) {
        D current = crudPort.load(id).orElseThrow(() -> notFound(id));
        mapper.updateDomain(current, cmd);
        current = beforeUpdate(current, cmd);      // <--- hook
        D saved = crudPort.save(current);
        afterSave(saved);                          // <--- hook
        return mapper.toResponse(saved);
    }

    @Override
    public void delete(ID id) {
        // You can add a beforeDelete hook if you need later.
        crudPort.delete(id);
    }

    @Override
    public R getById(ID id) {
        return mapper.toResponse(
                crudPort.load(id).orElseThrow(() -> notFound(id))
        );
    }

    @Override
    public Page<R> search(F filter, Pageable pageable) {
        return searchPort.search(filter, pageable).map(mapper::toResponse);
    }
}

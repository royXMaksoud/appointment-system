// CrudController.java
package com.sharedlib.core.web.controller;

import com.sharedlib.core.domain.ports.in.*;
import com.sharedlib.core.web.response.ApiResponse;
import com.sharedlib.core.web.response.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
public abstract class CrudController<ID, C, U, R, F> {

    protected final CreateUseCase<C, R> createUC;
    protected final UpdateUseCase<ID, U, R> updateUC;
    protected final DeleteUseCase<ID> deleteUC;
    protected final GetByIdUseCase<ID, R> getByIdUC;
    protected final SearchUseCase<F, R> searchUC;

    protected CrudController(CreateUseCase<C, R> createUC,
                             UpdateUseCase<ID, U, R> updateUC,
                             DeleteUseCase<ID> deleteUC,
                             GetByIdUseCase<ID, R> getByIdUC,
                             SearchUseCase<F, R> searchUC) {
        this.createUC = createUC;
        this.updateUC = updateUC;
        this.deleteUC = deleteUC;
        this.getByIdUC = getByIdUC;
        this.searchUC = searchUC;
    }

    @PostMapping
    public ApiResponse<R> create(@RequestBody @Valid C req) {
        return ApiResponse.ok(createUC.create(req));
    }

    @PutMapping("/{id}")
    public ApiResponse<R> update(@PathVariable ID id, @RequestBody @Valid U req) {
        return ApiResponse.ok(updateUC.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable ID id) {
        deleteUC.delete(id);
        return ApiResponse.ok(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<R> getById(@PathVariable ID id) {
        return ApiResponse.ok(getByIdUC.getById(id));
    }

    @GetMapping
    public PageResponse<R> search(F filter, Pageable pageable) {
        Page<R> page = searchUC.search(filter, pageable);
        return PageResponse.from(page);
    }
}

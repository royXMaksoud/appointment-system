// BaseMapper.java
package com.sharedlib.core.application.mapper;

public interface BaseMapper<D, C, U, R> {
    D fromCreate(C createRequest);
    void updateDomain(D target, U updateRequest);
    R toResponse(D domain);
}

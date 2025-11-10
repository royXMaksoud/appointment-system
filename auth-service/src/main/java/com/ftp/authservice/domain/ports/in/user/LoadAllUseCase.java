package com.ftp.authservice.domain.ports.in.user;

import com.ftp.authservice.domain.model.User;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface LoadAllUseCase
        extends com.sharedlib.core.domain.ports.in.SearchUseCase<FilterRequest, User> {

    default Page<User> loadAllUsers(FilterRequest filter, Pageable pageable) {
        return search(filter, pageable);
    }
}
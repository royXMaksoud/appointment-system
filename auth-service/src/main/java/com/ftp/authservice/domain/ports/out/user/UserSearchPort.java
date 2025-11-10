package com.ftp.authservice.domain.ports.out.user;

import com.ftp.authservice.domain.model.User;
import com.sharedlib.core.domain.ports.out.SearchPort;
import com.sharedlib.core.filter.FilterRequest;


public interface UserSearchPort extends SearchPort<User, FilterRequest> {}

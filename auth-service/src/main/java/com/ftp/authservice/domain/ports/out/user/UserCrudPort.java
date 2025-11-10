package com.ftp.authservice.domain.ports.out.user;




import com.ftp.authservice.domain.model.User;
import com.sharedlib.core.domain.ports.out.CrudPort;

import java.util.UUID;


public interface UserCrudPort extends CrudPort<User, UUID> {}

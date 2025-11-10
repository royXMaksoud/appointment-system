package com.ftp.authservice.infrastructure.adapters;

import com.ftp.authservice.domain.model.User;
import com.ftp.authservice.domain.ports.out.user.UserCrudPort;
import com.ftp.authservice.domain.ports.out.user.UserSearchPort;
import com.ftp.authservice.infrastructure.config.UserFilterConfig;
import com.ftp.authservice.infrastructure.db.entities.UserJpaEntity;

import com.ftp.authservice.infrastructure.db.mappers.UserMapper;
import com.ftp.authservice.infrastructure.db.repositories.UserRepository;
import com.sharedlib.core.filter.FilterRequest;
import com.sharedlib.core.filter.GenericSpecificationBuilder;
import com.sharedlib.core.persistence.adapter.BaseJpaAdapter;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Component;

import java.util.UUID;



@Component
public class UserDbAdapter
        extends BaseJpaAdapter<User, UserJpaEntity, UUID, FilterRequest>
        implements UserCrudPort, UserSearchPort {


    public UserDbAdapter(UserRepository repository, UserMapper mapper) {
        super(repository, repository, mapper);
    }

    @Override

    protected Specification<UserJpaEntity> buildSpecification(FilterRequest filter) {
        if (filter == null ||
                ((filter.getCriteria() == null || filter.getCriteria().isEmpty()) &&
                        (filter.getGroups() == null || filter.getGroups().isEmpty()))) {

            return (root, q, cb) -> cb.conjunction();
        }

        return new GenericSpecificationBuilder<UserJpaEntity>(UserFilterConfig.ALLOWED_FIELDS)
                .withCriteria(filter.getCriteria())
                .withGroups(filter.getGroups())
                .withScopes(filter.getScopes())
                .build();
    }

}

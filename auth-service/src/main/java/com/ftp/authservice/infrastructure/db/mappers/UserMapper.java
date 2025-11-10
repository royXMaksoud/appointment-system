// file: auth-service/src/main/java/com/ftp/authservice/infrastructure/db/mappers/UserMapper.java
package com.ftp.authservice.infrastructure.db.mappers;

import com.ftp.authservice.domain.model.User;
import com.ftp.authservice.domain.model.UserType;
import com.ftp.authservice.infrastructure.db.entities.UserJpaEntity;
import com.sharedlib.core.persistence.mapper.DomainEntityMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper extends DomainEntityMapper<User, UserJpaEntity> {

    // Domain -> Entity
    @Override
    @Mappings({
            @Mapping(target = "password", source = "passwordHash"),           // field rename
            @Mapping(target = "type", expression = "java(toTypeString(domain.getType()))")
    })
    UserJpaEntity toEntity(User domain);

    // Entity -> Domain
    @Override
    @Mappings({
            @Mapping(target = "passwordHash", source = "password"),           // field rename
            @Mapping(target = "type", expression = "java(toUserType(entity.getType()))")
    })
    User toDomain(UserJpaEntity entity);

    @Override
    @Mappings({
            @Mapping(target = "password", source = "passwordHash"),
            @Mapping(target = "type", expression = "java(toTypeString(source.getType()))")
    })
    void updateEntity(@MappingTarget UserJpaEntity target, User source);

    // helpers
    default String toTypeString(UserType t) {
        return t != null ? t.name() : "USER";
    }
    default UserType toUserType(String s) {
        try { return s != null ? UserType.valueOf(s) : UserType.USER; }
        catch (Exception ignore) { return UserType.USER; }
    }
}

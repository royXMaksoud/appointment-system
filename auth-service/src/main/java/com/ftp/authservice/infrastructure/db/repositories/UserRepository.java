package com.ftp.authservice.infrastructure.db.repositories;

import com.ftp.authservice.infrastructure.db.entities.UserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository
        extends JpaRepository<UserJpaEntity, UUID>, JpaSpecificationExecutor<UserJpaEntity> {
    Optional<UserJpaEntity> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);

    @Query("""
        SELECT u FROM UserJpaEntity u
        WHERE LOWER(u.email) = LOWER(:email)
          AND u.enabled = true
          AND u.deleted = false
    """)
    Optional<UserJpaEntity> findByEmailIgnoreCaseAndEnabledTrueAndDeletedFalse(String email);


}
/*

public interface UserRepository extends JpaRepository<UserJpaEntity, UUID> {

    // Reads
    Optional<UserJpaEntity> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    Optional<UserJpaEntity> findByEmailIgnoreCaseAndEnabledTrueAndDeletedFalse(String email);

    // Search with pagination (JPQL + count query)
    @Query(
            value = """
                select u from UserJpaEntity u
                where u.deleted = false
                  and (
                       lower(u.email) like lower(concat('%', :q, '%'))
                    or lower(u.fullName) like lower(concat('%', :q, '%'))
                  )
                """,
            countQuery = """
                     select count(u) from UserJpaEntity u
                     where u.deleted = false
                       and (
                            lower(u.email) like lower(concat('%', :q, '%'))
                         or lower(u.fullName) like lower(concat('%', :q, '%'))
                       )
                     """
    )
    Page<UserJpaEntity> search(@Param("q") String q, Pageable pageable);

    // Updates (modifying queries)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("update UserJpaEntity u set u.deleted = true, u.enabled = false where u.id = :id")
    int softDelete(@Param("id") UUID id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("update UserJpaEntity u set u.enabled = :enabled where u.id = :id and u.deleted = false")
    int setEnabled(@Param("id") UUID id, @Param("enabled") boolean enabled);
}
*/

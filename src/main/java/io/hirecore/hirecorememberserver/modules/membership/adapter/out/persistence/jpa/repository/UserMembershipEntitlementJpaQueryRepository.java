package io.hirecore.hirecorememberserver.modules.membership.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.membership.adapter.out.persistence.jpa.entity.UserMembershipEntitlementJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserMembershipEntitlementJpaQueryRepository extends Repository<UserMembershipEntitlementJpaEntity, Long> {
    @Query("""
        SELECT u.storageQuotaBytesSnapshot
        FROM UserMembershipEntitlementJpaEntity u
        WHERE u.memberAccountId = :memberAccountId
    """)
    Optional<Long> findStorageQuotaBytesSnapshotByMemberAccountId(@Param("memberAccountId") Long memberAccountId);
}

package io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.entity.UserStorageUsageJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserStorageUsageJpaQueryRepository extends Repository<UserStorageUsageJpaEntity, Long> {
    @Query("""
        SELECT usu.usedQuotaBytes
        FROM UserStorageUsageJpaEntity usu
        WHERE usu.memberAccountId =:memberAccountId
    """)
    Optional<Long> findUsedQuotaBytesByMemberAccountId(@Param("memberAccountId") Long memberAccountId);
}

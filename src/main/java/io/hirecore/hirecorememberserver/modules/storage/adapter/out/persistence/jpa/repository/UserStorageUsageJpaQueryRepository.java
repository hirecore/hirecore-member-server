package io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.entity.UserStorageUsageJpaEntity;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserStorageUsageJpaQueryRepository extends Repository<UserStorageUsageJpaEntity, Long> {
    Optional<UserStorageUsageJpaEntity> findByMemberAccountId(Long memberAccountId);
}

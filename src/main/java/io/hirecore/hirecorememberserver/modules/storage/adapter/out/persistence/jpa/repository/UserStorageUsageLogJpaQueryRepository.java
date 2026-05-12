package io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.entity.UserStorageUsageLogJpaEntity;
import org.springframework.data.repository.Repository;

public interface UserStorageUsageLogJpaQueryRepository extends Repository<UserStorageUsageLogJpaEntity, Long> {
    boolean existsByIdempotencyKey(String idempotencyKey);
}

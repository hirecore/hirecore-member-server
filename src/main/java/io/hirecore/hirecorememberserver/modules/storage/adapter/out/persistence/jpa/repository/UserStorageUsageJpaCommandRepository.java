package io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.entity.UserStorageUsageJpaEntity;
import org.springframework.data.repository.Repository;

public interface UserStorageUsageJpaCommandRepository extends Repository<UserStorageUsageJpaEntity, Long> {
    UserStorageUsageJpaEntity save(UserStorageUsageJpaEntity userStorageUsage);
}

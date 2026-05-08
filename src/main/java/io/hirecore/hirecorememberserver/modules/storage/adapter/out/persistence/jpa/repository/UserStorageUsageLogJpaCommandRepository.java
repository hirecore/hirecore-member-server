package io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.entity.UserStorageUsageLogJpaEntity;
import org.springframework.data.repository.Repository;

public interface UserStorageUsageLogJpaCommandRepository extends Repository<UserStorageUsageLogJpaEntity, Long> {
    UserStorageUsageLogJpaEntity save(UserStorageUsageLogJpaEntity userStorageUsageLog);
}

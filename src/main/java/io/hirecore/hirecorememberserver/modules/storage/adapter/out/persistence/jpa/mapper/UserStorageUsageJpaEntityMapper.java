package io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.entity.UserStorageUsageJpaEntity;
import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsage;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class UserStorageUsageJpaEntityMapper {
    public abstract UserStorageUsageJpaEntity toJpaEntity(UserStorageUsage domain);
    public abstract UserStorageUsage toDomain(UserStorageUsageJpaEntity entity);
}

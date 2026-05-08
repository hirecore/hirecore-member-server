package io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.entity.UserStorageUsageLogJpaEntity;
import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsageLog;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class UserStorageUsageLogJpaEntityMapper {
    public abstract UserStorageUsageLogJpaEntity toJpaEntity(UserStorageUsageLog domain);
    public abstract UserStorageUsageLog toDomain(UserStorageUsageLogJpaEntity entity);
}

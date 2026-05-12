package io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.entity.UserStorageUsageLogJpaEntity;
import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.mapper.UserStorageUsageLogJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.repository.UserStorageUsageLogJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.SaveUserStorageUsageLogPort;
import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsageLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStorageUsageLogJpaCommandAdapter implements SaveUserStorageUsageLogPort {

    private final UserStorageUsageLogJpaCommandRepository userStorageUsageLogJpaCommandRepository;
    private final UserStorageUsageLogJpaEntityMapper userStorageUsageLogJpaEntityMapper;

    @Override
    public UserStorageUsageLog save(UserStorageUsageLog userStorageUsageLog) {
        UserStorageUsageLogJpaEntity entity = userStorageUsageLogJpaEntityMapper.toJpaEntity(userStorageUsageLog);
        UserStorageUsageLogJpaEntity saved = userStorageUsageLogJpaCommandRepository.save(entity);
        return userStorageUsageLogJpaEntityMapper.toDomain(saved);
    }
}

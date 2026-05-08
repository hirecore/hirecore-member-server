package io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.entity.UserStorageUsageJpaEntity;
import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.entity.UserStorageUsageLogJpaEntity;
import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.mapper.UserStorageUsageJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.mapper.UserStorageUsageLogJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.repository.UserStorageUsageJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.repository.UserStorageUsageJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.repository.UserStorageUsageLogJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.SaveUserStorageUsageLogPort;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.SaveUserStorageUsagePort;
import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsage;
import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsageLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserStorageUsageJpaCommandAdapter implements SaveUserStorageUsagePort, SaveUserStorageUsageLogPort {

    private final UserStorageUsageJpaQueryRepository userStorageUsageJpaQueryRepository;
    private final UserStorageUsageJpaCommandRepository userStorageUsageJpaCommandRepository;
    private final UserStorageUsageLogJpaCommandRepository userStorageUsageLogJpaCommandRepository;
    private final UserStorageUsageJpaEntityMapper userStorageUsageJpaEntityMapper;
    private final UserStorageUsageLogJpaEntityMapper userStorageUsageLogJpaEntityMapper;

    @Override
    public UserStorageUsage save(UserStorageUsage userStorageUsage) {
        Optional<UserStorageUsageJpaEntity> existing =
                userStorageUsageJpaQueryRepository.findByMemberAccountId(userStorageUsage.getMemberAccountId());

        UserStorageUsageJpaEntity entity;
        if (existing.isPresent()) {
            entity = existing.get();
            entity.applyUsedQuotaBytes(
                    userStorageUsage.getUsedQuotaBytes(),
                    userStorageUsage.getAuditingInfo().updatedAt()
            );
        } else {
            UserStorageUsageJpaEntity fresh = userStorageUsageJpaEntityMapper.toJpaEntity(userStorageUsage);
            entity = userStorageUsageJpaCommandRepository.save(fresh);
        }

        return userStorageUsageJpaEntityMapper.toDomain(entity);
    }

    @Override
    public UserStorageUsageLog save(UserStorageUsageLog userStorageUsageLog) {
        UserStorageUsageLogJpaEntity entity = userStorageUsageLogJpaEntityMapper.toJpaEntity(userStorageUsageLog);
        UserStorageUsageLogJpaEntity saved = userStorageUsageLogJpaCommandRepository.save(entity);
        return userStorageUsageLogJpaEntityMapper.toDomain(saved);
    }
}

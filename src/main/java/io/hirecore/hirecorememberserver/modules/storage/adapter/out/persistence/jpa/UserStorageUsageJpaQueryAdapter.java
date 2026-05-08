package io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.mapper.UserStorageUsageJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.repository.UserStorageUsageJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.LoadUserStorageUsagePort;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.LoadUserUsedQuotaPort;
import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserStorageUsageJpaQueryAdapter implements LoadUserUsedQuotaPort, LoadUserStorageUsagePort {

    private final UserStorageUsageJpaQueryRepository userStorageUsageJpaQueryRepository;
    private final UserStorageUsageJpaEntityMapper userStorageUsageJpaEntityMapper;

    @Override
    public Long getUsedQuotaBytes(Long memberAccountId) {
        return userStorageUsageJpaQueryRepository.findUsedQuotaBytesByMemberAccountId(memberAccountId)
                .orElse(0L);
    }

    @Override
    public Optional<UserStorageUsage> findByMemberAccountId(Long memberAccountId) {
        return userStorageUsageJpaQueryRepository.findByMemberAccountId(memberAccountId)
                .map(userStorageUsageJpaEntityMapper::toDomain);
    }
}

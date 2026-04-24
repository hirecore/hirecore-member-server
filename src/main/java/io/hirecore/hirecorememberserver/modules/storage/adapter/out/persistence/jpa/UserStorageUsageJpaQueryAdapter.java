package io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.repository.UserStorageUsageJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.LoadUserUsedQuotaPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStorageUsageJpaQueryAdapter implements LoadUserUsedQuotaPort {

    private final UserStorageUsageJpaQueryRepository userStorageUsageJpaQueryRepository;

    @Override
    public Long getBytes(Long memberAccountId) {
        return userStorageUsageJpaQueryRepository.findUsedQuotaBytesByMemberAccountId(memberAccountId)
                .orElse(0L);
    }
}

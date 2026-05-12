package io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.repository.UserStorageUsageLogJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.LoadUserStorageUsageLogPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStorageUsageLogJpaQueryAdapter implements LoadUserStorageUsageLogPort {

    private final UserStorageUsageLogJpaQueryRepository userStorageUsageLogJpaQueryRepository;

    @Override
    public boolean existsByIdempotencyKey(String idempotencyKey) {
        return userStorageUsageLogJpaQueryRepository.existsByIdempotencyKey(idempotencyKey);
    }
}

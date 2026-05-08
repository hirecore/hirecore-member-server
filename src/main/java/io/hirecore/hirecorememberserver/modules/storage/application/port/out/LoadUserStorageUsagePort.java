package io.hirecore.hirecorememberserver.modules.storage.application.port.out;

import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsage;

import java.util.Optional;

public interface LoadUserStorageUsagePort {
    Optional<UserStorageUsage> findByMemberAccountId(Long memberAccountId);
}

package io.hirecore.hirecorememberserver.modules.storage.application.port.out;

import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsage;

public interface SaveUserStorageUsagePort {
    UserStorageUsage save(UserStorageUsage userStorageUsage);
}

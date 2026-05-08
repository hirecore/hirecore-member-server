package io.hirecore.hirecorememberserver.modules.storage.application.port.out;

import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsageLog;

public interface SaveUserStorageUsageLogPort {
    UserStorageUsageLog save(UserStorageUsageLog userStorageUsageLog);
}

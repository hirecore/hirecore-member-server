package io.hirecore.hirecorememberserver.modules.storage.application.port.out;

public interface LoadUserStorageUsageLogPort {
    boolean existsByIdempotencyKey(String idempotencyKey);
}

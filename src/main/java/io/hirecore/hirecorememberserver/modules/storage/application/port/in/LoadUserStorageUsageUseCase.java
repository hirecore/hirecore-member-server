package io.hirecore.hirecorememberserver.modules.storage.application.port.in;

public interface LoadUserStorageUsageUseCase {
    Long execute(Long memberAccountId);
}

package io.hirecore.hirecorememberserver.modules.storage.application.port.in;

import java.util.Collection;

public interface SaveUserStorageUsageUseCase {
    void execute(Long memberAccountId, Collection<Long> imageIds);
}

package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

import java.util.Collection;

public interface SaveUserStorageUsagePort {
    void save(Long memberAccountId, Collection<Long> imageIds);
}

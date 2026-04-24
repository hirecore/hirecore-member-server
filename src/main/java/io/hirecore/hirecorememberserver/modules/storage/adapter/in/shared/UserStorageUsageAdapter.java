package io.hirecore.hirecorememberserver.modules.storage.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.storage.application.port.in.LoadUserStorageUsageUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadUserStorageUsagePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStorageUsageAdapter implements LoadUserStorageUsagePort {

    private final LoadUserStorageUsageUseCase userStorageUsageUseCase;

    @Override
    public Long getBytes(Long memberAccountId) {
        return userStorageUsageUseCase.execute(memberAccountId);
    }
}

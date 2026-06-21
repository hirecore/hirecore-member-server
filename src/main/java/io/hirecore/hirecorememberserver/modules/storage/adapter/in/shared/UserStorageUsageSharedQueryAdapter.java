package io.hirecore.hirecorememberserver.modules.storage.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.storage.application.port.in.LoadUserStorageUsageUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadUserUsedQuotaPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStorageUsageSharedQueryAdapter implements LoadUserUsedQuotaPort {

    private final LoadUserStorageUsageUseCase userStorageUsageUseCase;

    @Override
    public Long findUsedQuotaBytes(Long memberAccountId) {
        return userStorageUsageUseCase.execute(memberAccountId);
    }
}

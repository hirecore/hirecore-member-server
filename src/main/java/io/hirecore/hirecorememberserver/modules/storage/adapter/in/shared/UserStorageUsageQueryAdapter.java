package io.hirecore.hirecorememberserver.modules.storage.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.storage.application.port.in.LoadUserStorageUsageUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadUserUsedQuotaPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStorageUsageQueryAdapter implements LoadUserUsedQuotaPort {

    private final LoadUserStorageUsageUseCase userStorageUsageUseCase;

    @Override
    public Long getUsedQuotaBytes(Long memberAccountId) {
        return userStorageUsageUseCase.execute(memberAccountId);
    }
}

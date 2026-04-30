package io.hirecore.hirecorememberserver.modules.storage.application.usecase;

import io.hirecore.hirecorememberserver.modules.storage.application.port.in.LoadUserStorageUsageUseCase;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.LoadUserUsedQuotaPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoadUserStorageUsageUseCaseImpl implements LoadUserStorageUsageUseCase {

    private final LoadUserUsedQuotaPort loadUserUsedQuotaPort;

    @Override
    @Transactional(readOnly = true)
    public Long execute(Long memberAccountId) {
        return loadUserUsedQuotaPort.getUsedQuotaBytes(memberAccountId);
    }
}

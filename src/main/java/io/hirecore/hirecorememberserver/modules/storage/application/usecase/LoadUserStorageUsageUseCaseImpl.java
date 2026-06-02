package io.hirecore.hirecorememberserver.modules.storage.application.usecase;

import io.hirecore.hirecorememberserver.modules.storage.application.port.in.LoadUserStorageUsageUseCase;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.LoadUserStorageUsagePort;
import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoadUserStorageUsageUseCaseImpl implements LoadUserStorageUsageUseCase {

    private final LoadUserStorageUsagePort loadUserStorageUsagePort;

    @Override
    @Transactional(readOnly = true)
    public Long execute(Long memberAccountId) {
        return loadUserStorageUsagePort.findByMemberAccountId(memberAccountId)
                .map(UserStorageUsage::getUsedQuotaBytes)
                .orElse(0L);
    }
}

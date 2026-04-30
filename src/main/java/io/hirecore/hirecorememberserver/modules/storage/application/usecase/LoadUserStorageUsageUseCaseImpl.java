package io.hirecore.hirecorememberserver.modules.storage.application.usecase;

import io.hirecore.hirecorememberserver.modules.storage.application.port.in.LoadUserStorageUsageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoadUserStorageUsageUseCaseImpl implements LoadUserStorageUsageUseCase {



    @Override
    @Transactional(readOnly = true)
    public Long execute(Long memberAccountId) {
        return 0L;
    }
}

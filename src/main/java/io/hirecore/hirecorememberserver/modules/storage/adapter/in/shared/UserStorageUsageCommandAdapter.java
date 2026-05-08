package io.hirecore.hirecorememberserver.modules.storage.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.storage.application.port.in.SaveUserStorageUsageUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.SaveUserStorageUsagePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class UserStorageUsageCommandAdapter implements SaveUserStorageUsagePort {

    private final SaveUserStorageUsageUseCase saveUserStorageUsageUseCase;

    @Override
    public void save(Long memberAccountId, Collection<Long> imageIds) {
        saveUserStorageUsageUseCase.execute(memberAccountId, imageIds);
    }
}

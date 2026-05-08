package io.hirecore.hirecorememberserver.modules.storage.application.usecase;

import io.hirecore.hirecorememberserver.modules.storage.application.port.in.SaveUserStorageUsageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class SaveUserStorageUsageUseCaseImpl implements SaveUserStorageUsageUseCase {

    @Override
    public void execute(Long memberAccountId, Collection<Long> imageIds) {

    }
}

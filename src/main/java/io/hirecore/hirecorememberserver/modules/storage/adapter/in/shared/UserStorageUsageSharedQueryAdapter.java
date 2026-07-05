package io.hirecore.hirecorememberserver.modules.storage.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.storage.application.exception.UserStorageApplicationException;
import io.hirecore.hirecorememberserver.modules.storage.application.exception.UserStorageApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.storage.application.port.in.LoadUserStorageUsageUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadUserStorageLimitSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadUserUsedQuotaSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.VerifyUserStorageCapacitySharedPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStorageUsageSharedQueryAdapter implements VerifyUserStorageCapacitySharedPort
{

    private final LoadUserStorageUsageUseCase loadUserStorageUsageUseCase;
    private final LoadUserStorageLimitSharedPort loadUserStorageLimitPort;


    @Override
    public void verifyCapacityFor(Long memberAccountId, Long uploadFileSizeBytes) {
        Long storageSnapshotBytes = loadUserStorageLimitPort.findStorageLimitBytes(memberAccountId);
        Long usedStorageBytes = loadUserStorageUsageUseCase.execute(memberAccountId);

        if (uploadFileSizeBytes + usedStorageBytes > storageSnapshotBytes) {
            throw new UserStorageApplicationException(
                    UserStorageApplicationExceptionCodeCluster.DetailResponse.STORAGE_QUOTA_EXCEEDED
            );
        }
    }
}

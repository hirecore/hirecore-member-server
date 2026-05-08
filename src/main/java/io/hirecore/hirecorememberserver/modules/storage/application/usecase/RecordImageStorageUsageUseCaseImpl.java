package io.hirecore.hirecorememberserver.modules.storage.application.usecase;

import io.hirecore.hirecorememberserver.modules.storage.application.port.in.RecordImageStorageUsageUseCase;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.LoadUserStorageUsagePort;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.SaveUserStorageUsageLogPort;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.SaveUserStorageUsagePort;
import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsage;
import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsageLog;
import io.hirecore.hirecorememberserver.modules.storage.domain.vo.ResourceKind;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecordImageStorageUsageUseCaseImpl implements RecordImageStorageUsageUseCase {

    private static final String IDEMPOTENCY_KEY_PREFIX = "image-uploaded:";

    private final LoadUserStorageUsagePort loadUserStorageUsagePort;
    private final SaveUserStorageUsagePort saveUserStorageUsagePort;
    private final SaveUserStorageUsageLogPort saveUserStorageUsageLogPort;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute(Long memberAccountId, Long imageFileMetaId, ResourceKind resourceKind, Long fileSizeBytes) {
        UserStorageUsage current = loadUserStorageUsagePort.findByMemberAccountId(memberAccountId)
                .orElseGet(() -> UserStorageUsage.createForMember(memberAccountId));

        Long beforeUsedQuotaBytes = current.getUsedQuotaBytes();
        UserStorageUsage updated = current.increase(fileSizeBytes);
        UserStorageUsage saved = saveUserStorageUsagePort.save(updated);

        UserStorageUsageLog log = UserStorageUsageLog.createForResourceCreation(
                memberAccountId,
                resourceKind,
                imageFileMetaId,
                fileSizeBytes,
                beforeUsedQuotaBytes,
                saved.getUsedQuotaBytes(),
                buildIdempotencyKey(imageFileMetaId)
        );
        saveUserStorageUsageLogPort.save(log);
    }

    private static String buildIdempotencyKey(Long imageFileMetaId) {
        return IDEMPOTENCY_KEY_PREFIX + imageFileMetaId;
    }
}

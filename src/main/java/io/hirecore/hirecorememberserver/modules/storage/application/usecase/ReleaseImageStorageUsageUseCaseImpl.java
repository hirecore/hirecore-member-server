package io.hirecore.hirecorememberserver.modules.storage.application.usecase;

import io.hirecore.hirecorememberserver.modules.storage.application.port.in.ReleaseImageStorageUsageUseCase;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.LoadUserStorageUsageLogPort;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.LoadUserStorageUsagePort;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.SaveUserStorageUsageLogPort;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.SaveUserStorageUsagePort;
import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsage;
import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsageLog;
import io.hirecore.hirecorememberserver.modules.storage.domain.vo.ResourceKind;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReleaseImageStorageUsageUseCaseImpl implements ReleaseImageStorageUsageUseCase {

    private static final String IDEMPOTENCY_KEY_PREFIX = "image-orphaned:";

    private final LoadUserStorageUsagePort loadUserStorageUsagePort;
    private final LoadUserStorageUsageLogPort loadUserStorageUsageLogPort;
    private final SaveUserStorageUsagePort saveUserStorageUsagePort;
    private final SaveUserStorageUsageLogPort saveUserStorageUsageLogPort;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute(Long memberAccountId, Long imageFileMetaId, ResourceKind resourceKind, Long fileSizeBytes) {
        String idempotencyKey = buildIdempotencyKey(imageFileMetaId);

        if (loadUserStorageUsageLogPort.existsByIdempotencyKey(idempotencyKey)) {
            return;
        }

        Optional<UserStorageUsage> maybeCurrent = loadUserStorageUsagePort.findByMemberAccountId(memberAccountId);
        if (maybeCurrent.isEmpty()) {
            // 사용량 row 없음 = 정합성 사고, 흐름 막지 않고 경고만
            log.warn(
                    "회수할 사용량 row 가 없습니다 — skip. memberAccountId={}, imageFileMetaId={}",
                    memberAccountId, imageFileMetaId
            );
            return;
        }
        UserStorageUsage current = maybeCurrent.get();

        Long beforeUsedQuotaBytes = current.getUsedQuotaBytes();
        UserStorageUsage updated = current.decrease(fileSizeBytes);
        UserStorageUsage saved = saveUserStorageUsagePort.save(updated);

        UserStorageUsageLog usageLog = UserStorageUsageLog.createForResourceDeletion(
                memberAccountId,
                resourceKind,
                imageFileMetaId,
                -fileSizeBytes,
                beforeUsedQuotaBytes,
                saved.getUsedQuotaBytes(),
                idempotencyKey
        );
        saveUserStorageUsageLogPort.save(usageLog);
    }

    private static String buildIdempotencyKey(Long imageFileMetaId) {
        return IDEMPOTENCY_KEY_PREFIX + imageFileMetaId;
    }
}

package io.hirecore.hirecorememberserver.modules.storage.adapter.in.event;

import io.hirecore.hirecorememberserver.modules.storage.application.port.in.ReleaseImageStorageUsageUseCase;
import io.hirecore.hirecorememberserver.modules.storage.domain.vo.ResourceKind;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.ImageOrphanedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

// ImageOrphanedEvent 구독해 사용량 차감 (ORPHANED 커밋 후 AFTER_COMMIT, 멱등 키 race 흡수)
@Slf4j
@Component
@RequiredArgsConstructor
public class ImageOrphanedListener {

    private final ReleaseImageStorageUsageUseCase releaseImageStorageUsageUseCase;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleImageOrphaned(ImageOrphanedEvent event) {
        ResourceKind resourceKind = ResourceKind.resolve(event.domainType(), event.purpose());
        try {
            releaseImageStorageUsageUseCase.execute(
                    event.memberAccountId(),
                    event.imageFileMetaId(),
                    resourceKind,
                    event.fileSizeBytes()
            );
        } catch (DataIntegrityViolationException e) {
            log.debug(
                    "멱등 키 race — 이미 처리됨, 스킵: imageFileMetaId={}",
                    event.imageFileMetaId()
            );
        }
    }
}

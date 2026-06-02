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

/**
 * {@link ImageOrphanedEvent} 를 구독하여 사용자 스토리지 사용량을 차감합니다.
 *
 * <p>file BC 가 ORPHANED 전이를 커밋한 직후 AFTER_COMMIT 페이즈에서 동작합니다.
 * 멱등 키 race 로 인한 {@link DataIntegrityViolationException} 은 흡수합니다.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImageOrphanedEventListener {

    private final ReleaseImageStorageUsageUseCase releaseImageStorageUsageUseCase;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ImageOrphanedEvent event) {
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

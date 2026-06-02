package io.hirecore.hirecorememberserver.modules.storage.adapter.in.event;

import io.hirecore.hirecorememberserver.modules.storage.application.port.in.RecordImageStorageUsageUseCase;
import io.hirecore.hirecorememberserver.modules.storage.domain.vo.ResourceKind;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.ImageUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageUploadedEventListener {

    private final RecordImageStorageUsageUseCase recordImageStorageUsageUseCase;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ImageUploadedEvent event) {
        ResourceKind resourceKind = ResourceKind.resolve(event.domainType(), event.purpose());
        try {
            recordImageStorageUsageUseCase.execute(
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

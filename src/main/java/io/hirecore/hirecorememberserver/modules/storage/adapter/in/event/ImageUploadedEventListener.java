package io.hirecore.hirecorememberserver.modules.storage.adapter.in.event;

import io.hirecore.hirecorememberserver.modules.storage.application.port.in.RecordImageStorageUsageUseCase;
import io.hirecore.hirecorememberserver.modules.storage.domain.vo.ResourceKind;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.ImageUploadedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ImageUploadedEventListener {

    /**
     * 1차 구현은 PORTFOLIO_CONTENT 단일 매핑으로 처리합니다.
     * 추후 ImageUploadedEvent에 domainType이 추가되면 도메인 타입 기반 분기로 확장합니다.
     */
    private static final ResourceKind RESOURCE_KIND = ResourceKind.PORTFOLIO_CONTENT;

    private final RecordImageStorageUsageUseCase recordImageStorageUsageUseCase;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ImageUploadedEvent event) {
        recordImageStorageUsageUseCase.execute(
                event.memberAccountId(),
                event.imageFileMetaId(),
                RESOURCE_KIND,
                event.fileSizeBytes()
        );
    }
}

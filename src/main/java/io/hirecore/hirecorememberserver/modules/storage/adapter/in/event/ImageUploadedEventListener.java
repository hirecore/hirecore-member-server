package io.hirecore.hirecorememberserver.modules.storage.adapter.in.event;

import io.hirecore.hirecorememberserver.modules.storage.application.port.in.RecordImageStorageUsageUseCase;
import io.hirecore.hirecorememberserver.modules.storage.domain.vo.ResourceKind;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.ImageUploadedEvent;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.DomainType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Purpose;
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
        ResourceKind resourceKind = resolveResourceKind(event.domainType(), event.purpose());
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

    /**
     * publisher 의 도메인 분류 정보를 storage BC 자체 분류({@link ResourceKind})로 해석합니다.
     *
     * <p>매핑 규칙은 storage BC 의 책임이며, file BC 의 도메인 정보 변경과 독립적으로 진화합니다.</p>
     */
    private static ResourceKind resolveResourceKind(DomainType domainType, Purpose purpose) {
        return switch (domainType) {
            case PORTFOLIO -> switch (purpose) {
                case CONTENT_IMAGE   -> ResourceKind.PORTFOLIO_CONTENT;
                case THUMBNAIL_IMAGE -> ResourceKind.PORTFOLIO_THUMBNAIL;
            };
            case RESUME -> switch (purpose) {
                case CONTENT_IMAGE   -> ResourceKind.RESUME_CONTENT;
                case THUMBNAIL_IMAGE -> ResourceKind.RESUME_ATTACHMENT;
            };
        };
    }
}

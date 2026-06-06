package io.hirecore.hirecorememberserver.modules.file.adapter.in.event;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.MarkImagesAsOrphanedUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.PortfolioImagesUnlinkedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * {@link PortfolioImagesUnlinkedEvent} 를 구독하여, 본문/썸네일에서 빠진 이미지들을 ORPHANED 로 전이시키는 핸들러입니다.
 *
 * <p>BEFORE_COMMIT 페이즈로 구독하여 포트폴리오 수정 트랜잭션과 ATOMIC 하게 처리됩니다.
 * ORPHANED 전이가 실패하면 포트폴리오 수정 자체가 롤백되어, 본문 src 와 메타 상태가 어긋나는 사고를 방지합니다.</p>
 *
 * <p>전이 결과로 {@code ImageOrphanedEvent} 가 발행되며, storage BC 의 {@code ImageOrphanedEventListener}
 * 가 AFTER_COMMIT 으로 받아 사용자 스토리지 사용량을 차감합니다.</p>
 */
@Component
@RequiredArgsConstructor
public class PortfolioImagesUnlinkedImageOrphaningHandler {

    private final MarkImagesAsOrphanedUseCase markImagesAsOrphanedUseCase;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(PortfolioImagesUnlinkedEvent event) {
        markImagesAsOrphanedUseCase.execute(event.memberAccountId(), event.imageFileMetaIds());
    }
}

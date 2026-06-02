package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.event;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.RecordPortfolioViewUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.event.PortfolioViewedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * {@link PortfolioViewedEvent}를 구독하여 {@code PortfolioMemberView} 생성·저장 및
 * 포트폴리오 캐시 조회수 증가를 처리하는 이벤트 핸들러입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PortfolioViewedMemberViewHandler {

    private final RecordPortfolioViewUseCase recordPortfolioViewUseCase;

    /**
     * 포트폴리오 조회 이벤트를 수신하여 {@code PortfolioMemberView}를 생성하고 캐시 조회수를 증가시킵니다.
     *
     * <p>발행 측은 readOnly 트랜잭션으로 동작하므로
     * {@link org.springframework.transaction.event.TransactionalEventListener @TransactionalEventListener}
     * 의 {@link TransactionPhase#AFTER_COMMIT} 단계로 분리해 별도 쓰기 트랜잭션에서 처리합니다.
     * 동일 사용자의 동시 요청으로 UNIQUE 제약이 위반되면 멱등성을 위해 스킵합니다.</p>
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePortfolioMemberViewCreation(PortfolioViewedEvent event) {
        try {
            recordPortfolioViewUseCase.execute(event.portfolioId(), event.viewerMemberAccountId());
        } catch (DataIntegrityViolationException e) {
            log.debug(
                    "PortfolioMemberView UNIQUE 충돌 — 동시 요청으로 이미 기록됨, 스킵: portfolioId={}, viewerId={}",
                    event.portfolioId(),
                    event.viewerMemberAccountId()
            );
        }
    }
}

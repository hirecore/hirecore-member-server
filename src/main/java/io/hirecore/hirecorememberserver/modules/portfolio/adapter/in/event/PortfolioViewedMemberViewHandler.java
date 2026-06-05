package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.event;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.IncrementPortfolioViewCountPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.SavePortfolioMemberViewPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioMemberView;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.event.PortfolioViewedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * {@link PortfolioViewedEvent}를 구독하여 {@code PortfolioMemberView} 생성·저장 및
 * 포트폴리오 캐시 조회수 증가를 처리하는 이벤트 핸들러입니다.
 *
 * <p>발행 측 트랜잭션이 readOnly 이므로 {@link TransactionPhase#AFTER_COMMIT} 단계에서
 * {@link Propagation#REQUIRES_NEW} 로 별도 쓰기 트랜잭션을 띄워 영구화한다.
 * 정책 판단(소유자 제외 / 첫 조회만 카운트)은 이미 {@code Portfolio.markViewedBy} 에서
 * 결정되어 본 이벤트로 통과한 사건이므로, 핸들러는 영구화에만 집중한다.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PortfolioViewedMemberViewHandler {

    private final SavePortfolioMemberViewPort savePortfolioMemberViewPort;
    private final IncrementPortfolioViewCountPort incrementPortfolioViewCountPort;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePortfolioMemberViewCreation(PortfolioViewedEvent event) {
        try {
            PortfolioMemberView view = PortfolioMemberView.create(event.viewerMemberAccountId());
            savePortfolioMemberViewPort.save(event.portfolioId(), view);
            incrementPortfolioViewCountPort.incrementById(event.portfolioId());
        } catch (DataIntegrityViolationException e) {
            log.debug(
                    "PortfolioMemberView UNIQUE 충돌 — 동시 요청으로 이미 기록됨, 스킵: portfolioId={}, viewerId={}",
                    event.portfolioId(),
                    event.viewerMemberAccountId()
            );
        }
    }
}

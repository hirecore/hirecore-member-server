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

// 조회 이벤트로 view 저장+조회수 증가 (발행 측 readOnly라 AFTER_COMMIT + REQUIRES_NEW로 쓰기)
@Slf4j
@Component
@RequiredArgsConstructor
public class PortfolioViewedListener {

    private final SavePortfolioMemberViewPort savePortfolioMemberViewPort;
    private final IncrementPortfolioViewCountPort incrementPortfolioViewCountPort;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePortfolioViewed(PortfolioViewedEvent event) {
        try {
            PortfolioMemberView view = PortfolioMemberView.create(event.viewerMemberAccountId());
            savePortfolioMemberViewPort.save(event.portfolioId(), view);
            incrementPortfolioViewCountPort.incrementViewCountById(event.portfolioId());
        } catch (DataIntegrityViolationException e) {
            log.debug(
                    "PortfolioMemberView UNIQUE 충돌 — 동시 요청으로 이미 기록됨, 스킵: portfolioId={}, viewerId={}",
                    event.portfolioId(),
                    event.viewerMemberAccountId()
            );
        }
    }
}

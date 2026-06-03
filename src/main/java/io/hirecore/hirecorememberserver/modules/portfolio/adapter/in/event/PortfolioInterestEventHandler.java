package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.event;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.DecrementPortfolioInterestCountPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.DeletePortfolioMemberInterestPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.IncrementPortfolioInterestCountPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.SavePortfolioMemberInterestPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioMemberInterest;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.event.PortfolioInterestCancelledEvent;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.event.PortfolioInterestRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 관심 등록/해제 도메인 이벤트를 구독하여 {@code PortfolioMemberInterest} 영구화와
 * 포트폴리오 캐시 관심수 갱신을 처리합니다.
 *
 * <p>발행 측 트랜잭션이 write 경로이므로 {@link EventListener}로 outer tx 내 동기 실행하여
 * 정책 판단(AR)과 영구화(핸들러)가 동일 트랜잭션 경계에 묶이도록 한다.</p>
 */
@Component
@RequiredArgsConstructor
public class PortfolioInterestEventHandler {

    private final SavePortfolioMemberInterestPort savePortfolioMemberInterestPort;
    private final IncrementPortfolioInterestCountPort incrementPortfolioInterestCountPort;
    private final DeletePortfolioMemberInterestPort deletePortfolioMemberInterestPort;
    private final DecrementPortfolioInterestCountPort decrementPortfolioInterestCountPort;

    @EventListener
    public void handlePortfolioInterestRegistered(PortfolioInterestRegisteredEvent event) {
        PortfolioMemberInterest interest = PortfolioMemberInterest.create(event.memberAccountId());
        savePortfolioMemberInterestPort.save(event.portfolioId(), interest);
        incrementPortfolioInterestCountPort.incrementInterestCountById(event.portfolioId());
    }

    @EventListener
    public void handlePortfolioInterestCancelled(PortfolioInterestCancelledEvent event) {
        boolean deleted = deletePortfolioMemberInterestPort.deleteBy(event.portfolioId(), event.memberAccountId());
        if (deleted) {
            decrementPortfolioInterestCountPort.decrementInterestCountById(event.portfolioId());
        }
    }
}

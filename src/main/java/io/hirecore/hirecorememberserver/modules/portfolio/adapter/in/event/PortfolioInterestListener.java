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

// 관심 등록/해제 이벤트로 영구화+캐시 갱신 (write 경로라 @EventListener로 outer tx 내 동기 실행)
@Component
@RequiredArgsConstructor
public class PortfolioInterestListener {

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

package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.RegisterPortfolioInterestUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.ExistsPortfolioMemberInterestPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterPortfolioInterestUseCaseImpl implements RegisterPortfolioInterestUseCase {

    private final LoadPortfolioPort loadPortfolioPort;
    private final ExistsPortfolioMemberInterestPort existsPortfolioMemberInterestPort;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public void execute(Long portfolioId, Long memberAccountId) {
        Portfolio portfolio = loadPortfolioPort.findPortfolio(portfolioId)
                .orElseThrow(() -> new PortfolioApplicationException(
                        PortfolioApplicationExceptionCodeCluster.DetailResponse.INTEREST_PORTFOLIO_NOT_FOUND
                ));
        boolean alreadyInterested = existsPortfolioMemberInterestPort.exists(portfolioId, memberAccountId);
        portfolio.registerInterestBy(memberAccountId, alreadyInterested);
        portfolio.pollAllEvents().forEach(applicationEventPublisher::publishEvent);
    }
}

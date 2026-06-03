package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.CancelPortfolioInterestUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelPortfolioInterestUseCaseImpl implements CancelPortfolioInterestUseCase {

    private final LoadPortfolioPort loadPortfolioPort;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public void execute(Long portfolioId, Long memberAccountId) {
        Portfolio portfolio = loadPortfolioPort.findPortfolio(portfolioId)
                .orElseThrow(() -> new PortfolioApplicationException(
                        PortfolioApplicationExceptionCodeCluster.InterestResponse.INTEREST_PORTFOLIO_NOT_FOUND
                ));
        portfolio.cancelInterestBy(memberAccountId);
        portfolio.pollAllEvents().forEach(applicationEventPublisher::publishEvent);
    }
}

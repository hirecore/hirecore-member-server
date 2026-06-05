package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.CancelPortfolioInterestUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.PublishDomainEventsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelPortfolioInterestUseCaseImpl implements CancelPortfolioInterestUseCase {

    private final LoadPortfolioPort loadPortfolioPort;
    private final PublishDomainEventsPort publishDomainEventsPort;

    @Override
    @Transactional
    public void execute(Long portfolioId, Long memberAccountId) {
        Portfolio portfolio = loadPortfolioPort.findPortfolio(portfolioId)
                .orElseThrow(() -> new PortfolioApplicationException(
                        PortfolioApplicationExceptionCodeCluster.DetailResponse.INTEREST_PORTFOLIO_NOT_FOUND
                ));
        portfolio.cancelInterestBy(memberAccountId);
        publishDomainEventsPort.publishAll(portfolio);
    }
}

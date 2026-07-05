package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.RegisterPortfolioInterestUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.ExistsPortfolioMemberInterestPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.PublishDomainEventsSharedPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterPortfolioInterestUseCaseImpl implements RegisterPortfolioInterestUseCase {

    private final LoadPortfolioPort loadPortfolioPort;
    private final ExistsPortfolioMemberInterestPort existsPortfolioMemberInterestPort;
    private final PublishDomainEventsSharedPort publishDomainEventsPort;

    @Override
    @Transactional
    public void execute(Long portfolioId, Long memberAccountId) {
        Portfolio portfolio = loadPortfolioPort.findById(portfolioId)
                .orElseThrow(() -> new PortfolioApplicationException(
                        PortfolioApplicationExceptionCodeCluster.DetailResponse.INTEREST_PORTFOLIO_NOT_FOUND
                ));
        boolean alreadyInterested = existsPortfolioMemberInterestPort.exists(portfolioId, memberAccountId);
        portfolio.registerInterestBy(memberAccountId, alreadyInterested);
        publishDomainEventsPort.publishAll(portfolio);
    }
}

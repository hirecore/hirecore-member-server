package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.RegisterPortfolioInterestUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.ExistsPortfolioMemberInterestPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.IncrementPortfolioInterestCountPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.SavePortfolioMemberInterestPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioMemberInterest;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterPortfolioInterestUseCaseImpl implements RegisterPortfolioInterestUseCase {

    private final LoadPortfolioPort loadPortfolioPort;
    private final ExistsPortfolioMemberInterestPort existsPortfolioMemberInterestPort;
    private final SavePortfolioMemberInterestPort savePortfolioMemberInterestPort;
    private final IncrementPortfolioInterestCountPort incrementPortfolioInterestCountPort;

    @Override
    @Transactional
    public void execute(Long portfolioId, Long memberAccountId) {
        Portfolio portfolio = loadPortfolioPort.findPortfolio(portfolioId)
                .orElseThrow(() -> new PortfolioApplicationException(
                        PortfolioApplicationExceptionCodeCluster.InterestResponse.INTEREST_PORTFOLIO_NOT_FOUND
                ));

        if (portfolio.getMemberAccountId().equals(memberAccountId)) {
            throw new PortfolioApplicationException(
                    PortfolioApplicationExceptionCodeCluster.InterestResponse.INTEREST_OWNER_NOT_ALLOWED
            );
        }
        if (portfolio.getVisibility() != Visibility.PUBLIC) {
            throw new PortfolioApplicationException(
                    PortfolioApplicationExceptionCodeCluster.InterestResponse.INTEREST_PORTFOLIO_FORBIDDEN
            );
        }
        if (existsPortfolioMemberInterestPort.exists(portfolioId, memberAccountId)) {
            return;
        }

        PortfolioMemberInterest interest = PortfolioMemberInterest.create(memberAccountId);
        savePortfolioMemberInterestPort.save(portfolioId, interest);
        incrementPortfolioInterestCountPort.incrementInterestCountById(portfolioId);
    }
}

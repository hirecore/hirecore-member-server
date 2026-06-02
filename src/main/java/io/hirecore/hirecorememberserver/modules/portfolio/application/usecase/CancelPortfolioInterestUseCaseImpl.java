package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.CancelPortfolioInterestUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.DecrementPortfolioInterestCountPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.DeletePortfolioMemberInterestPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelPortfolioInterestUseCaseImpl implements CancelPortfolioInterestUseCase {

    private final DeletePortfolioMemberInterestPort deletePortfolioMemberInterestPort;
    private final DecrementPortfolioInterestCountPort decrementPortfolioInterestCountPort;

    @Override
    @Transactional
    public void execute(Long portfolioId, Long memberAccountId) {
        boolean deleted = deletePortfolioMemberInterestPort.deleteBy(portfolioId, memberAccountId);
        if (deleted) {
            decrementPortfolioInterestCountPort.decrementInterestCountById(portfolioId);
        }
    }
}

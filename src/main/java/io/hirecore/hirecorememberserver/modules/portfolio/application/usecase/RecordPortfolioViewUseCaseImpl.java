package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.RecordPortfolioViewUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.IncrementPortfolioViewCountPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.SavePortfolioMemberViewPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioMemberView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecordPortfolioViewUseCaseImpl implements RecordPortfolioViewUseCase {

    private final SavePortfolioMemberViewPort savePortfolioMemberViewPort;
    private final IncrementPortfolioViewCountPort incrementPortfolioViewCountPort;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute(Long portfolioId, Long viewerMemberAccountId) {
        PortfolioMemberView view = PortfolioMemberView.create(viewerMemberAccountId);
        savePortfolioMemberViewPort.save(portfolioId, view);
        incrementPortfolioViewCountPort.incrementById(portfolioId);
    }
}

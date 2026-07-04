package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.assembler.PortfolioEditAssembler;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioEditUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoadPortfolioEditUseCaseImpl implements LoadPortfolioEditUseCase {

    private final LoadPortfolioPort loadPortfolioPort;
    private final PortfolioEditAssembler portfolioEditAssembler;

    @Override
    @Transactional(readOnly = true)
    public Response execute(Long portfolioId, Long viewerId) {
        Portfolio portfolio = loadPortfolio(portfolioId);
        ensureOwner(portfolio, viewerId);
        return portfolioEditAssembler.buildResponse(portfolio);
    }

    private Portfolio loadPortfolio(Long portfolioId) {
        return loadPortfolioPort.findById(portfolioId)
                .orElseThrow(() -> new PortfolioApplicationException(
                        PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NOT_FOUND
                ));
    }

    private void ensureOwner(Portfolio portfolio, Long viewerId) {
        if (!portfolio.isOwnedBy(viewerId)) {
            throw new PortfolioApplicationException(
                    PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_FORBIDDEN
            );
        }
    }
}

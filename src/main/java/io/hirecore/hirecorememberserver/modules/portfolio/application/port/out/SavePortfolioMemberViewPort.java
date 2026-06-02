package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioMemberView;

public interface SavePortfolioMemberViewPort {
    PortfolioMemberView save(Long portfolioId, PortfolioMemberView portfolioMemberView);
}

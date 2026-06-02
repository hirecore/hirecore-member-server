package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioMemberInterest;

public interface SavePortfolioMemberInterestPort {
    PortfolioMemberInterest save(Long portfolioId, PortfolioMemberInterest portfolioMemberInterest);
}

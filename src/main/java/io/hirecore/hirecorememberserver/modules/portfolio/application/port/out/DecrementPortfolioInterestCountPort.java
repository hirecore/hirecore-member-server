package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

public interface DecrementPortfolioInterestCountPort {
    void decrementInterestCountById(Long portfolioId);
}

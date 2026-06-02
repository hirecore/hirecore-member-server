package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

public interface IncrementPortfolioInterestCountPort {
    void incrementInterestCountById(Long portfolioId);
}

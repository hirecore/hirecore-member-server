package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

public interface IncrementPortfolioViewCountPort {
    void incrementViewCountById(Long portfolioId);
}

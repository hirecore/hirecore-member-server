package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in;

public interface CancelPortfolioInterestUseCase {
    void execute(Long portfolioId, Long memberAccountId);
}

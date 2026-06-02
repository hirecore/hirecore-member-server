package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in;

public interface RegisterPortfolioInterestUseCase {
    void execute(Long portfolioId, Long memberAccountId);
}

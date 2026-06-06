package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in;

public interface DeletePortfolioUseCase {
    void execute(Long portfolioId, Long viewerId);
}

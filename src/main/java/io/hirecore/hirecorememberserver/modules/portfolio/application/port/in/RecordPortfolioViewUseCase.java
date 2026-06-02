package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in;

public interface RecordPortfolioViewUseCase {
    void execute(Long portfolioId, Long viewerMemberAccountId);
}

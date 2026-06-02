package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

public interface ExistsPortfolioMemberViewPort {
    boolean exists(Long portfolioId, Long memberAccountId);
}

package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

public interface ExistsPortfolioMemberInterestPort {
    boolean exists(Long portfolioId, Long memberAccountId);
}

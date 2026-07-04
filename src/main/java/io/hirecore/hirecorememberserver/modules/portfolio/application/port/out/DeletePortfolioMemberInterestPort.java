package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

public interface DeletePortfolioMemberInterestPort {
    // 관심 등록 삭제, 삭제된 행 있으면 true
    boolean deleteBy(Long portfolioId, Long memberAccountId);
}

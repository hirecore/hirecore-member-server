package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

public interface DeletePortfolioMemberInterestPort {
    /**
     * 주어진 포트폴리오에 대한 회원의 관심 등록을 삭제한다.
     *
     * @return 삭제된 행이 1건 이상이면 {@code true}, 대상이 없어 변경이 없으면 {@code false}
     */
    boolean deleteBy(Long portfolioId, Long memberAccountId);
}

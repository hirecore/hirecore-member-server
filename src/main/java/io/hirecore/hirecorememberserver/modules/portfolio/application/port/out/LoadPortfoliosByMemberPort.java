package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;

import java.util.List;

public interface LoadPortfoliosByMemberPort {

    /**
     * 한 회원이 작성한 모든 포트폴리오를 마지막 수정 시각 내림차순으로 반환한다.
     */
    List<Portfolio> findAllByMemberAccountIdOrderByUpdatedAtDesc(Long memberAccountId);
}

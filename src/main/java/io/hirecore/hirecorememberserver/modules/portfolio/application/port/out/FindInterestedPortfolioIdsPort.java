package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

import java.util.Collection;
import java.util.Set;

public interface FindInterestedPortfolioIdsPort {

    // 주어진 포트폴리오들 중, 해당 회원이 관심 등록한 것들의 ID 집합 (목록용 배치 조회 · N+1 회피)
    Set<Long> findInterestedPortfolioIds(Collection<Long> portfolioIds, Long memberAccountId);
}

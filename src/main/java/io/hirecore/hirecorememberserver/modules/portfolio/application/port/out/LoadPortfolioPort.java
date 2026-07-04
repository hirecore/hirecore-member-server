package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;

import java.util.List;
import java.util.Optional;

public interface LoadPortfolioPort {

    Optional<Portfolio> findById(Long portfolioId);

    // 회원의 전체 포트폴리오를 수정 시각 내림차순으로
    List<Portfolio> findAllByMemberAccountIdOrderByUpdatedAtDesc(Long memberAccountId);

    // 상세 페이지 "다른 작품"용: 지정 식별자 제외한 PUBLIC 포트폴리오 (PRIVATE 항상 제외)
    List<Portfolio> findAllPublicByMemberAccountIdExcludingOrderByUpdatedAtDesc(
            Long memberAccountId,
            Long excludedPortfolioId
    );
}

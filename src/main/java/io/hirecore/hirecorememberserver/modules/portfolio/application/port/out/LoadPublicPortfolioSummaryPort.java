package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;

import java.time.Instant;
import java.util.List;

public interface LoadPublicPortfolioSummaryPort {

    // 공개 포트폴리오를 effective updatedAt/portfolioId 내림차순 커서 페이징 (두 커서값은 함께 전달)
    List<PublicPortfolioRow> findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(
            Instant cursorEffectiveUpdatedAt,
            Long cursorPortfolioId,
            int limit
    );

    // 서브 집계까지 로드된 도메인 + 정렬/노출용 GREATEST updatedAt
    record PublicPortfolioRow(
            Portfolio portfolio,
            Instant effectiveUpdatedAt
    ) {}
}

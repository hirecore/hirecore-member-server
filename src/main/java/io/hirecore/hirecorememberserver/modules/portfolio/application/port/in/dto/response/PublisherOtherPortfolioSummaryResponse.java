package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * 포트폴리오 상세 응답의 publisher 섹션에 포함되는 "작성자의 다른 작품" 요약 항목.
 *
 * <p>다른 사람에게 노출되는 영역이므로 PUBLIC 포트폴리오만 포함되며,
 * 본 포트폴리오 자체는 제외된다.</p>
 */
public record PublisherOtherPortfolioSummaryResponse(
        Long portfolioId,
        String title,
        List<PortfolioJobCategoryResponse> jobCategories,
        Long viewCount,
        Long interestCount,
        Instant updatedAt
) {
}

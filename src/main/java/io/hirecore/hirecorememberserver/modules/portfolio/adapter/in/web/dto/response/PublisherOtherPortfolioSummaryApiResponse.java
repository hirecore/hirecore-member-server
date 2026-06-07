package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response;

import io.hirecore.hirecorememberserver.common.web.json.TsidId;

import java.time.Instant;
import java.util.List;

public record PublisherOtherPortfolioSummaryApiResponse(
        @TsidId Long portfolioId,
        String title,
        List<PortfolioJobCategoryApiResponse> jobCategories,
        Long viewCount,
        Long interestCount,
        Instant updatedAt
) {
}

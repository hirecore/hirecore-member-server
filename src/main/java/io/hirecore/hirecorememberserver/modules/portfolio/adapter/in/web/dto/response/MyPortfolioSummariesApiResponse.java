package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response;

import java.util.List;

public record MyPortfolioSummariesApiResponse(
        List<MyPortfolioSummaryItemApiResponse> items
) {
}

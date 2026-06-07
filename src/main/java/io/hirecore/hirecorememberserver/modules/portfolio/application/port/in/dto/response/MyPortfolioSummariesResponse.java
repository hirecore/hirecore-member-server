package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response;

import java.util.List;

public record MyPortfolioSummariesResponse(
        List<MyPortfolioSummaryItemResponse> items
) {
}

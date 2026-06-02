package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response;

import io.hirecore.hirecorememberserver.common.web.json.TsidId;

public record UpdatePortfolioApiResponse(
        @TsidId Long portfolioId
) {
}

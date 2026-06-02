package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response;

import io.hirecore.hirecorememberserver.common.web.json.TsidId;

public record PortfolioJobCategoryApiResponse(
        @TsidId Long id,
        Long depth,
        String categoryCode,
        String name
) {
}

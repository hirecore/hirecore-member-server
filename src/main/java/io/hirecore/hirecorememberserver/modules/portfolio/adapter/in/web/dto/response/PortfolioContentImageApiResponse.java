package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response;

import io.hirecore.hirecorememberserver.common.web.json.TsidId;

public record PortfolioContentImageApiResponse(
        @TsidId Long imageId,
        String url
) {
}

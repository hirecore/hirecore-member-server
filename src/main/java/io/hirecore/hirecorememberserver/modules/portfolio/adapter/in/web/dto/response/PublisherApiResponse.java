package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response;

import java.util.List;

public record PublisherApiResponse(
        String nickname,
        List<PublisherOtherPortfolioSummaryApiResponse> otherPortfolios
) {
}

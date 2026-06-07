package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response;

import java.util.List;

public record PublisherResponse(
        String nickname,
        List<PublisherOtherPortfolioSummaryResponse> otherPortfolios
) {
}

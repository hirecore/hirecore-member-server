package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response;

public record PortfolioTagApiResponse(
        String userInputTag,
        Integer sortOrder
) {
}

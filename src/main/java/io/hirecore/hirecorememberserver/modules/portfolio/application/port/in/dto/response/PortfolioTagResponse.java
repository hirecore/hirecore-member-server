package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response;

public record PortfolioTagResponse(
        String userInputTag,
        Integer sortOrder
) {
}

package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response;

public record PortfolioJobCategoryResponse(
        Long id,
        Long depth,
        String categoryCode,
        String name
) {
}
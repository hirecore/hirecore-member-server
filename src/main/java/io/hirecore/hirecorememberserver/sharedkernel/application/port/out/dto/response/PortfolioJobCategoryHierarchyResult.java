package io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response;

public record PortfolioJobCategoryHierarchyResult(
        Long id,
        Long depth,
        String categoryCode,
        String name
) {
}

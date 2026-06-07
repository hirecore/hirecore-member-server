package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.CollaborationTypeApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.VisibilityApiValue;

import java.util.List;

public record PortfolioBodyApiResponse(
        String title,
        CollaborationTypeApiValue collaborationType,
        VisibilityApiValue visibility,
        List<PortfolioJobCategoryApiResponse> jobCategories,
        List<PortfolioTagApiResponse> tags,
        List<PortfolioExternalLinkApiResponse> externalLinks,
        PortfolioContentApiResponse content
) {
}

package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.CollaborationTypeApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.VisibilityApiValue;

import java.util.List;

public record PortfolioEditApiResponse(
        String privateMemo,
        String previewSummary,
        String thumbnailImageUrl,
        List<PortfolioJobCategoryApiResponse> jobCategories,
        CollaborationTypeApiValue collaborationType,
        VisibilityApiValue visibility,
        String title,
        List<PortfolioTagApiResponse> tags,
        List<PortfolioExternalLinkApiResponse> externalLinks,
        PortfolioContentApiResponse content
) {
}

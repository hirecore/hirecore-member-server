package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.CollaborationTypeApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.VisibilityApiValue;

import java.time.Instant;
import java.util.List;

public record PortfolioDetailApiResponse(
        Boolean isOwner,
        String publisher,
        List<PortfolioJobCategoryApiResponse> jobCategories,
        CollaborationTypeApiValue collaborationType,
        VisibilityApiValue visibility,
        Long viewCount,
        Long interestCount,
        String title,
        List<PortfolioTagApiResponse> tags,
        List<PortfolioExternalLinkApiResponse> externalLinks,
        PortfolioContentApiResponse content,
        Instant updatedAt
) {
}

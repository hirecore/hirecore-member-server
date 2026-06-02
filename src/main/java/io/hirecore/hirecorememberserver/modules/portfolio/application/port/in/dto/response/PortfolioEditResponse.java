package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response;

import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;

import java.util.List;

public record PortfolioEditResponse(
        String privateMemo,
        String previewSummary,
        Long thumbnailImageId,
        String thumbnailImageUrl,
        List<PortfolioJobCategoryResponse> jobCategories,
        CollaborationType collaborationType,
        Visibility visibility,
        String title,
        List<PortfolioTagResponse> tags,
        List<PortfolioExternalLinkResponse> externalLinks,
        List<PortfolioContentImageResponse> contentImages,
        PortfolioContentResponse content
) {
}

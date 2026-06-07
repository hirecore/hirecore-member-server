package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response;

import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;

import java.util.List;

public record PortfolioBodyResponse(
        String title,
        CollaborationType collaborationType,
        Visibility visibility,
        List<PortfolioJobCategoryResponse> jobCategories,
        List<PortfolioTagResponse> tags,
        List<PortfolioExternalLinkResponse> externalLinks,
        PortfolioContentResponse content
) {
}

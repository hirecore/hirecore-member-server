package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response;

import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;

import java.time.Instant;
import java.util.List;

public record MyPortfolioSummaryItemResponse(
        Long portfolioId,
        String title,
        String previewSummary,
        String privateMemo,
        Long thumbnailImageId,
        String thumbnailImageUrl,
        List<PortfolioJobCategoryResponse> jobCategories,
        CollaborationType collaborationType,
        Visibility visibility,
        List<PortfolioTagResponse> tags,
        Long interestCount,
        LinkedResumeResponse linkedResume,
        LinkedCoverLetterResponse linkedCoverLetter,
        Instant updatedAt
) {
}

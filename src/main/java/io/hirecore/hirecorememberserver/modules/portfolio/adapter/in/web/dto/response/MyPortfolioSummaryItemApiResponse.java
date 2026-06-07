package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response;

import io.hirecore.hirecorememberserver.common.web.json.TsidId;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.CollaborationTypeApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.VisibilityApiValue;

import java.time.Instant;
import java.util.List;

public record MyPortfolioSummaryItemApiResponse(
        @TsidId Long portfolioId,
        String title,
        String previewSummary,
        String privateMemo,
        @TsidId Long thumbnailImageId,
        String thumbnailImageUrl,
        List<PortfolioJobCategoryApiResponse> jobCategories,
        CollaborationTypeApiValue collaborationType,
        VisibilityApiValue visibility,
        List<PortfolioTagApiResponse> tags,
        Long interestCount,
        LinkedResumeApiResponse linkedResume,
        LinkedCoverLetterApiResponse linkedCoverLetter,
        Instant updatedAt
) {
}

package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto;

import io.hirecore.hirecorememberserver.common.web.json.TsidId;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.SharedResponseApiDto;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.CollaborationTypeApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.VisibilityApiValue;

import java.util.List;

// GET /api/portfolios/{portfolioId}/edit — 편집 폼 초기화 web contract
public class LoadPortfolioEditApi {

    private LoadPortfolioEditApi() {}

    // 본문 이미지 (imageId, url) 매핑 (PUT 시 src로부터 imageId 역추적용)
    public record ContentImage(
            @TsidId Long imageId,
            String url
    ) {}

    public record Response(
            String privateMemo,
            String previewSummary,
            @TsidId Long thumbnailImageId,
            String thumbnailImageUrl,
            List<SharedResponseApiDto.JobCategory> jobCategories,
            CollaborationTypeApiValue collaborationType,
            VisibilityApiValue visibility,
            String title,
            List<SharedResponseApiDto.SequentialTag> tags,
            List<SharedResponseApiDto.ExternalLink> externalLinks,
            List<ContentImage> contentImages,
            SharedResponseApiDto.RichTextContent content
    ) {}
}

package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto;

import io.hirecore.hirecorememberserver.common.web.json.TsidId;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.SharedResponseApiDto;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.CollaborationTypeApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.VisibilityApiValue;

import java.util.List;

/**
 * GET /api/portfolios/{portfolioId}/edit — 작성자 본인 편집 폼 초기화용 endpoint 의 web 측 contract.
 *
 * <p>본문 image 노드의 src ↔ imageId 매핑을 위한 {@link ContentImage} 룩업 목록이 함께 응답된다.</p>
 */
public class LoadPortfolioEditApi {

    private LoadPortfolioEditApi() {}

    /** 본문에서 사용 중인 이미지의 (imageId, url) 매핑. 클라이언트가 PUT 요청 시 본문 image 노드 src 로부터 imageId 를 역추적하는 데 사용. */
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

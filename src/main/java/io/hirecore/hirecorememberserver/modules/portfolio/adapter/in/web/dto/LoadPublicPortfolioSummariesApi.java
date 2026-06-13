package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto;

import io.hirecore.hirecorememberserver.common.web.json.TsidId;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.SharedResponseApiDto;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.CollaborationTypeApiValue;

import java.time.Instant;
import java.util.List;

/**
 * GET /api/portfolios/summaries/public — 공개 포트폴리오 요약 목록(무한 스크롤) endpoint 의 web 측 contract.
 *
 * <p>응답은 {@link Item} 의 리스트와 {@link Pagination} 으로 구성된다.
 * 정렬 기준은 Portfolio / PortfolioContent / PortfolioJobCategory / PortfolioTag 의 시간 정보 중 가장 최신값(effective updatedAt) 의 내림차순이며,
 * 동일 시각에 대한 tie-break 은 {@code portfolioId} 내림차순으로 결정된다.</p>
 */
public class LoadPublicPortfolioSummariesApi {

    private LoadPublicPortfolioSummariesApi() {}

    public record Item(
            @TsidId Long portfolioId,
            SharedResponseApiDto.Thumbnail thumbnail,
            List<SharedResponseApiDto.JobCategory> jobCategories,
            String title,
            String previewSummary,
            CollaborationTypeApiValue collaborationType,
            List<SharedResponseApiDto.SequentialTag> tags,
            List<SharedResponseApiDto.ExternalLink> externalLinks,
            String nickname,
            Long viewCount,
            Long interestCount,
            Boolean isOwner,
            Instant updatedAt
    ){
    }

    /**
     * @param nextCursor 다음 페이지 요청 시 그대로 echo 할 opaque 토큰.
     *                   {@code hasNext == false} 인 경우 {@code null}.
     * @param hasNext    다음 페이지 존재 여부.
     */
    public record Pagination(
            String nextCursor,
            Boolean hasNext
    ) {}

    public record Response(
            List<Item> items,
            Pagination pagination
    ) {}
}

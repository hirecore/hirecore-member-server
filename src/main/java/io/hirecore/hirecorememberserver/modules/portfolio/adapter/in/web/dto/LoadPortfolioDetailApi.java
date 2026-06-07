package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto;

import io.hirecore.hirecorememberserver.common.web.json.TsidId;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.SharedResponseApiDto;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.CollaborationTypeApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.VisibilityApiValue;

import java.time.Instant;
import java.util.List;

/**
 * GET /api/portfolios/{portfolioId} — 포트폴리오 상세 조회 endpoint 의 web 측 contract.
 *
 * <p>응답은 viewer 의존 통계 + portfolio 본체 + publisher + 연결된 이력서/자기소개서 의
 * nested 구조로 구성된다.</p>
 */
public class LoadPortfolioDetailApi {

    private LoadPortfolioDetailApi() {}

    public record Body(
            String title,
            CollaborationTypeApiValue collaborationType,
            VisibilityApiValue visibility,
            List<SharedResponseApiDto.JobCategory> jobCategories,
            List<SharedResponseApiDto.SequentialTag> tags,
            List<SharedResponseApiDto.ExternalLink> externalLinks,
            SharedResponseApiDto.RichTextContent content
    ) {}

    public record Publisher(
            String nickname,
            List<OtherPortfolioSummary> otherPortfolios
    ) {
        public record OtherPortfolioSummary(
                @TsidId Long portfolioId,
                String title,
                List<SharedResponseApiDto.JobCategory> jobCategories,
                Long viewCount,
                Long interestCount,
                Instant updatedAt
        ) {}
    }

    /**
     * 연결된 이력서. 자원이 PRIVATE 이고 viewer 가 자원 소유자가 아니면 {@code content} 가 {@code null}.
     */
    public record LinkedResume(
            @TsidId Long id,
            String title,
            SharedResponseApiDto.RichTextContent content
    ) {}

    /**
     * 연결된 자기소개서. 자원이 PRIVATE 이고 viewer 가 자원 소유자가 아니면 {@code content} 가 {@code null}.
     */
    public record LinkedCoverLetter(
            @TsidId Long id,
            String title,
            SharedResponseApiDto.RichTextContent content
    ) {}

    public record Response(
            Boolean isOwner,
            Long viewCount,
            Long interestCount,
            Boolean isInterested,
            Instant updatedAt,
            Body portfolio,
            Publisher publisher,
            LinkedResume linkedResume,
            LinkedCoverLetter linkedCoverLetter
    ) {}
}

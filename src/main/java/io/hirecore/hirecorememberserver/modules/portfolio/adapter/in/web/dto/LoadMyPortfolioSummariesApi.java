package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto;

import io.hirecore.hirecorememberserver.common.web.json.TsidId;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.SharedResponseApiDto;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.CollaborationTypeApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.VisibilityApiValue;

import java.time.Instant;
import java.util.List;

/**
 * GET /api/portfolios/summaries/mine — 작성자 본인 보유 포트폴리오 요약 목록 endpoint 의 web 측 contract.
 *
 * <p>응답은 {@link Item} 의 리스트 wrapper. 연결된 이력서/자기소개서는 라벨용 메타({@code id}, {@code title}) 만 노출된다.</p>
 */
public class LoadMyPortfolioSummariesApi {

    private LoadMyPortfolioSummariesApi() {}

    /** 연결된 이력서의 라벨용 요약 (id + title). */
    public record LinkedResume(
            @TsidId Long id,
            String title
    ) {}

    /** 연결된 자기소개서의 라벨용 요약 (id + title). */
    public record LinkedCoverLetter(
            @TsidId Long id,
            String title
    ) {}

    public record Item(
            @TsidId Long portfolioId,
            String title,
            String previewSummary,
            String privateMemo,
            SharedResponseApiDto.Thumbnail thumbnail,
            List<SharedResponseApiDto.JobCategory> jobCategories,
            CollaborationTypeApiValue collaborationType,
            VisibilityApiValue visibility,
            List<SharedResponseApiDto.SequentialTag> tags,
            Long interestCount,
            LinkedResume linkedResume,
            LinkedCoverLetter linkedCoverLetter,
            Instant updatedAt
    ) {}

    public record Response(
            List<Item> items
    ) {}
}

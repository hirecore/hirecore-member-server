package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto;

import io.hirecore.hirecorememberserver.common.web.json.TsidId;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.SharedResponseApiDto;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.CollaborationTypeApiValue;

import java.time.Instant;
import java.util.List;

// GET /api/portfolios/summaries/public — 공개 포트폴리오 요약 목록 web contract
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
            Boolean isInterested,
            Instant updatedAt
    ){
    }

    // nextCursor: 다음 페이지 커서 (hasNext false면 null)
    public record Pagination(
            String nextCursor,
            Boolean hasNext
    ) {}

    public record Response(
            List<Item> items,
            Pagination pagination
    ) {}
}

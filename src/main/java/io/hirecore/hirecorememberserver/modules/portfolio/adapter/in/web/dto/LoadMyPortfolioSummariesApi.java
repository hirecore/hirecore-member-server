package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto;

import io.hirecore.hirecorememberserver.common.web.json.TsidId;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.SharedResponseApiDto;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.CollaborationTypeApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.VisibilityApiValue;

import java.time.Instant;
import java.util.List;

// GET /api/portfolios/summaries/mine — 본인 포트폴리오 요약 목록 web contract
public class LoadMyPortfolioSummariesApi {

    private LoadMyPortfolioSummariesApi() {}

    // 연결 이력서 라벨 요약
    public record LinkedResume(
            @TsidId Long id,
            String title
    ) {}

    // 연결 자기소개서 라벨 요약
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

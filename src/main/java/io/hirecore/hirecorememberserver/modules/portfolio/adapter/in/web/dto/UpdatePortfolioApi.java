package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto;

import io.hirecore.hirecorememberserver.common.web.json.TsidId;
import io.hirecore.hirecorememberserver.common.web.json.TsidIds;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.SharedRequestApiDto;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.CollaborationTypeApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.VisibilityApiValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * PUT /api/portfolios/{portfolioId} — 포트폴리오 수정 endpoint 의 web 측 contract.
 *
 * <p>전체 교체(PUT) 시맨틱. 편집 화면에서 사용자가 변경한 내용을 일괄 반영할 때 호출된다.</p>
 */
public class UpdatePortfolioApi {

    private UpdatePortfolioApi() {}

    public record Request(
            @Valid
            @NotNull(message = "카테고리는 필수입니다.")
            SharedRequestApiDto.LeafJobCategory leafJobCategory,

            @NotNull(message = "협업 유형은 필수입니다.")
            CollaborationTypeApiValue collaborationType,

            @NotNull(message = "공개 범위는 필수입니다.")
            VisibilityApiValue visibility,

            @NotBlank(message = "제목은 필수입니다.")
            String title,

            String privateMemo,

            @NotBlank(message = "한 줄 소개는 필수입니다.")
            @Size(max = 100, message = "한 줄 소개는 100자를 초과할 수 없습니다.")
            String previewSummary,

            @TsidId
            Long thumbnailImageId,

            @TsidIds
            List<Long> contentImageIds,

            @Valid
            List<SharedRequestApiDto.@NotNull SequentialTag> tags,

            @Valid
            List<SharedRequestApiDto.@NotNull ExternalLink> externalLinks,

            @Valid
            @NotNull(message = "포트폴리오 본문은 필수입니다.")
            SharedRequestApiDto.RichTextContent content,

            @TsidId
            Long linkedResumeId,

            @TsidId
            Long linkedCoverLetterId
    ) {}

    public record Response(
            @TsidId Long portfolioId
    ) {}
}

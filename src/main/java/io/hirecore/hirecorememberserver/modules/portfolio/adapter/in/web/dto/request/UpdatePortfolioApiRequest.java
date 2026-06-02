package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request;

import io.hirecore.hirecorememberserver.common.web.json.TsidId;
import io.hirecore.hirecorememberserver.common.web.json.TsidIds;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.CollaborationTypeApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.VisibilityApiValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdatePortfolioApiRequest(
        @Valid
        @NotNull(message = "카테고리는 필수입니다.")
        JobCategoryApiRequest jobCategory,

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
        List<@NotNull PortfolioTagApiRequest> tags,

        @Valid
        List<@NotNull PortfolioExternalLinkApiRequest> externalLinks,

        @Valid
        @NotNull(message = "포트폴리오 본문은 필수입니다.")
        PortfolioContentApiRequest content,

        @TsidId
        Long linkedResumeId,

        @TsidId
        Long linkedCoverLetterId
) {
}

package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request;

import io.hirecore.hirecorememberserver.common.web.json.TsidId;
import io.hirecore.hirecorememberserver.common.web.json.TsidIds;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreatePortfolioApiRequest(
        @NotBlank(message = "직무 카테고리 코드는 필수입니다.")
        String categoryCode,

        String customCategory,

        @NotNull(message = "협업 유형은 필수입니다.")
        CollaborationType collaborationType,

        @NotNull(message = "공개 범위는 필수입니다.")
        Visibility visibility,

        @NotBlank(message = "제목은 필수입니다.")
        String title,

        String privateMemo,

        @TsidId
        Long thumbnailImageId,

        @TsidIds
        List<Long> contentImageIds,

        List<String> tags,

        List<ExternalLink> externalLinks,

        @Valid
        @NotNull(message = "포트폴리오 본문은 필수입니다.")
        PortfolioContentApiRequest content,

        @TsidId
        Long linkedResumeId,

        @TsidId
        Long linkedCoverLetterId
) {
}

package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response;

import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record PortfolioDetailResponse(
        Boolean isOwner,
        String publisher,
        CollaborationType collaborationType,
        Visibility visibility,
        String title,
        List<PortfolioTagResponse> tags,
        List<PortfolioExternalLinkResponse> externalLinks,
        PortfolioContentResponse content,
        Instant updatedAt
) {
}

package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.CollaborationTypeApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.ExternalLinkApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.VisibilityApiValue;

import java.time.Instant;
import java.util.List;

public record PortfolioDetailApiResponse(
        Boolean isOwner,
        String publisher,
        CollaborationTypeApiValue collaborationType,
        VisibilityApiValue visibility,
        String title,
        PortfolioContentApiResponse content,
        List<PortfolioTagApiResponse> tags,
        List<ExternalLinkApiValue> externalLinks,
        Instant updatedAt
) {
}

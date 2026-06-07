package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response;

import java.time.Instant;

public record PortfolioDetailApiResponse(
        Boolean isOwner,
        Long viewCount,
        Long interestCount,
        Boolean isInterested,
        Instant updatedAt,
        PortfolioBodyApiResponse portfolio,
        PublisherApiResponse publisher,
        LinkedResumeContentApiResponse linkedResume,
        LinkedCoverLetterContentApiResponse linkedCoverLetter
) {
}

package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record PortfolioDetailResponse(
        Boolean isOwner,
        Long viewCount,
        Long interestCount,
        Boolean isInterested,
        Instant updatedAt,
        PortfolioBodyResponse portfolio,
        PublisherResponse publisher,
        LinkedResumeContentResponse linkedResume,
        LinkedCoverLetterContentResponse linkedCoverLetter
) {
}

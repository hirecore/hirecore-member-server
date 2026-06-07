package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in;

import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedResponseDto;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

public interface LoadPortfolioDetailUseCase {

    Response execute(Long portfolioId, Long viewerId);

    @Builder
    record Response(
            Boolean isOwner,
            Long viewCount,
            Long interestCount,
            Boolean isInterested,
            Instant updatedAt,
            Body portfolio,
            Publisher publisher,
            LinkedResume linkedResume,
            LinkedCoverLetter linkedCoverLetter
    ) {

        public record Body(
                String title,
                CollaborationType collaborationType,
                Visibility visibility,
                List<SharedResponseDto.JobCategory> jobCategories,
                List<SharedResponseDto.SequentialTag> tags,
                List<SharedResponseDto.ExternalLink> externalLinks,
                SharedResponseDto.RichTextContent content
        ) {}

        public record Publisher(
                String nickname,
                List<OtherPortfolioSummary> otherPortfolios
        ) {
            public record OtherPortfolioSummary(
                    Long portfolioId,
                    String title,
                    List<SharedResponseDto.JobCategory> jobCategories,
                    Long viewCount,
                    Long interestCount,
                    Instant updatedAt
            ) {}
        }

        /**
         * 연결된 이력서. 자원이 PRIVATE 이고 viewer 가 자원 소유자가 아니면 {@code content} 가 {@code null}.
         */
        public record LinkedResume(
                Long id,
                String title,
                SharedResponseDto.RichTextContent content
        ) {}

        /**
         * 연결된 자기소개서. 자원이 PRIVATE 이고 viewer 가 자원 소유자가 아니면 {@code content} 가 {@code null}.
         */
        public record LinkedCoverLetter(
                Long id,
                String title,
                SharedResponseDto.RichTextContent content
        ) {}
    }
}

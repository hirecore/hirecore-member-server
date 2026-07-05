package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in;

import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedResponseDto;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;

import java.time.Instant;
import java.util.List;

public interface LoadPublicPortfolioSummariesUseCase {

    // 공개 포트폴리오 한 페이지 조회 (viewerId null이면 비로그인, isOwner 판정용)
    Response execute(String cursorToken, int size, Long viewerId);

    record Response(
            List<Item> items,
            Pagination pagination
    ) {

        public record Item(
                Long portfolioId,
                SharedResponseDto.Thumbnail thumbnail,
                List<SharedResponseDto.JobCategory> jobCategories,
                String title,
                String previewSummary,
                CollaborationType collaborationType,
                List<SharedResponseDto.SequentialTag> tags,
                List<SharedResponseDto.ExternalLink> externalLinks,
                String nickname,
                Long viewCount,
                Long interestCount,
                Boolean isOwner,
                Boolean isInterested,
                Instant updatedAt
        ) {}

        // nextCursor: 다음 페이지 커서 (hasNext false면 null)
        public record Pagination(
                String nextCursor,
                Boolean hasNext
        ) {}
    }
}

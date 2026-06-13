package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in;

import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedResponseDto;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;

import java.time.Instant;
import java.util.List;

public interface LoadPublicPortfolioSummariesUseCase {

    /**
     * 공개 포트폴리오를 effective updatedAt 내림차순으로 한 페이지만큼 가져온다.
     *
     * @param cursorToken 클라이언트가 직전 응답에서 받은 opaque 커서 토큰.
     *                    첫 페이지 요청 시 {@code null}.
     * @param size        한 페이지 당 항목 수. 호출 측이 검증한 유효 범위만 들어온다.
     * @param viewerId    호출자(로그인 사용자) 의 memberAccountId. 비로그인 호출이면 {@code null}.
     *                    각 항목의 {@code isOwner} 판정에 사용된다.
     */
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
                Instant updatedAt
        ) {}

        /**
         * @param nextCursor 다음 페이지 요청 시 echo 할 opaque 토큰. {@code hasNext == false} 인 경우 {@code null}.
         * @param hasNext    다음 페이지 존재 여부.
         */
        public record Pagination(
                String nextCursor,
                Boolean hasNext
        ) {}
    }
}

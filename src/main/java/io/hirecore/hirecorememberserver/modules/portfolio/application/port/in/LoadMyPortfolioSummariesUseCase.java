package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in;

import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedResponseDto;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;

import java.time.Instant;
import java.util.List;

public interface LoadMyPortfolioSummariesUseCase {

    Response execute(Long viewerId);

    record Response(
            List<Item> items
    ) {

        public record Item(
                Long portfolioId,
                String title,
                String previewSummary,
                String privateMemo,
                SharedResponseDto.Thumbnail thumbnail,
                List<SharedResponseDto.JobCategory> jobCategories,
                CollaborationType collaborationType,
                Visibility visibility,
                List<SharedResponseDto.SequentialTag> tags,
                Long interestCount,
                LinkedResume linkedResume,
                LinkedCoverLetter linkedCoverLetter,
                Instant updatedAt
        ) {}

        // 연결 이력서 라벨 요약
        public record LinkedResume(
                Long id,
                String title
        ) {}

        // 연결 자기소개서 라벨 요약
        public record LinkedCoverLetter(
                Long id,
                String title
        ) {}
    }
}

package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in;

import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedResponseDto;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;

import java.util.List;

public interface LoadPortfolioEditUseCase {

    Response execute(Long portfolioId, Long viewerId);

    record Response(
            String privateMemo,
            String previewSummary,
            Long thumbnailImageId,
            String thumbnailImageUrl,
            List<SharedResponseDto.JobCategory> jobCategories,
            CollaborationType collaborationType,
            Visibility visibility,
            String title,
            List<SharedResponseDto.SequentialTag> tags,
            List<SharedResponseDto.ExternalLink> externalLinks,
            List<ContentImage> contentImages,
            SharedResponseDto.RichTextContent content
    ) {

        // 본문 이미지 id ↔ url 매핑 (편집 화면 룩업용)
        public record ContentImage(
                Long imageId,
                String url
        ) {}
    }
}

package io.hirecore.hirecorememberserver.modules.portfolio.application.assembler;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioEditUseCase.Response;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioJobCategory;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedResponseDto;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadImageUrlPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

// 포트폴리오 편집 응답 조립 (썸네일·본문 이미지 URL 해소, 직무 계층 조회)
@Component
@RequiredArgsConstructor
public class PortfolioEditAssembler {

    private final LoadJobCategoryPort loadJobCategoryPort;
    private final LoadImageUrlPort loadImageUrlPort;

    public Response buildResponse(Portfolio portfolio) {
        String thumbnailImageUrl = loadImageUrlPort.findUrlById(portfolio.getThumbnailImageId())
                .orElse(null);
        List<Response.ContentImage> contentImages = resolveContentImages(
                portfolio.getPortfolioContent().getImageIds()
        );
        SharedResponseDto.RichTextContent content = new SharedResponseDto.RichTextContent(
                portfolio.getPortfolioContent().getContentJson(),
                portfolio.getPortfolioContent().getContentHtml()
        );

        return new Response(
                portfolio.getPrivateMemo(),
                portfolio.getPreviewSummary(),
                portfolio.getThumbnailImageId(),
                thumbnailImageUrl,
                toJobCategoriesResponse(portfolio.getPortfolioJobCategory()),
                portfolio.getCollaborationType(),
                portfolio.getVisibility(),
                portfolio.getTitle(),
                toTagResponses(portfolio.getPortfolioTags()),
                toExternalLinkResponses(portfolio.getExternalLinks()),
                contentImages,
                content
        );
    }

    // 본문 imageId를 publicUrl과 매핑 (URL 해석 실패 항목은 제외)
    private List<Response.ContentImage> resolveContentImages(List<Long> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) {
            return List.of();
        }
        return imageIds.stream()
                .map(imageId -> loadImageUrlPort.findUrlById(imageId)
                        .map(url -> new Response.ContentImage(imageId, url))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    private List<SharedResponseDto.ExternalLink> toExternalLinkResponses(List<ExternalLink> links) {
        if (links == null || links.isEmpty()) {
            return List.of();
        }
        return links.stream()
                .map(link -> new SharedResponseDto.ExternalLink(link.label(), link.url()))
                .toList();
    }

    private List<SharedResponseDto.SequentialTag> toTagResponses(List<PortfolioTag> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        return tags.stream()
                .map(tag -> new SharedResponseDto.SequentialTag(tag.getName(), tag.getSortOrder()))
                .toList();
    }

    private List<SharedResponseDto.JobCategory> toJobCategoriesResponse(PortfolioJobCategory portfolioJobCategory) {
        AssertionUtils.notNull(
                portfolioJobCategory,
                PortfolioApplicationExceptionCodeCluster.DetailResponse.JOB_CATEGORY_NOT_FOUND,
                PortfolioApplicationException::new
        );

        return loadJobCategoryPort.findJobCategoryHierarchy(portfolioJobCategory.getLeafJobCategoryId())
                .stream()
                .map(hierarchy -> new SharedResponseDto.JobCategory(
                        hierarchy.id(),
                        hierarchy.depth(),
                        hierarchy.categoryCode(),
                        hierarchy.name()
                ))
                .toList();
    }
}

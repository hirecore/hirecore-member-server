package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioEditUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.*;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioJobCategory;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadImageUrlPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoadPortfolioEditUseCaseImpl implements LoadPortfolioEditUseCase {

    private final LoadPortfolioPort loadPortfolioPort;
    private final LoadJobCategoryPort loadJobCategoryPort;
    private final LoadImageUrlPort loadImageUrlPort;

    @Override
    @Transactional(readOnly = true)
    public PortfolioEditResponse execute(Long portfolioId, Long viewerId) {
        Portfolio portfolio = loadPortfolio(portfolioId);
        ensureOwner(portfolio, viewerId);

        String thumbnailImageUrl = loadImageUrlPort.findUrlById(portfolio.getThumbnailImageId())
                .orElse(null);

        List<PortfolioContentImageResponse> contentImages = resolveContentImages(
                portfolio.getPortfolioContent().getImageIds()
        );

        return buildResponse(
                portfolio,
                thumbnailImageUrl,
                contentImages,
                toTagResponses(portfolio.getPortfolioTags()),
                toJobCategoriesResponse(portfolio.getPortfolioJobCategory())
        );
    }

    /**
     * 본문 imageId 들을 publicUrl 과 함께 묶어 응답합니다.
     * 클라이언트는 이 매핑으로 편집 진입 시 url ↔ imageId 룩업 테이블을 초기화하며,
     * PUT 요청 시 본문 내 image 노드 src 로부터 imageId 를 역추적합니다.
     * URL 해석에 실패한 항목은 묶지 못하므로 응답에서 제외합니다.
     */
    private List<PortfolioContentImageResponse> resolveContentImages(List<Long> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) {
            return List.of();
        }
        return imageIds.stream()
                .map(imageId -> loadImageUrlPort.findUrlById(imageId)
                        .map(url -> new PortfolioContentImageResponse(imageId, url))
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    private Portfolio loadPortfolio(Long portfolioId) {
        return loadPortfolioPort.findPortfolio(portfolioId)
                .orElseThrow(() -> new PortfolioApplicationException(
                        PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NOT_FOUND
                ));
    }

    private void ensureOwner(Portfolio portfolio, Long viewerId) {
        if (viewerId == null || !portfolio.getMemberAccountId().equals(viewerId)) {
            throw new PortfolioApplicationException(
                    PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_FORBIDDEN
            );
        }
    }

    private PortfolioEditResponse buildResponse(
            Portfolio portfolio,
            String thumbnailImageUrl,
            List<PortfolioContentImageResponse> contentImages,
            List<PortfolioTagResponse> tags,
            List<PortfolioJobCategoryResponse> jobCategories
    ) {
        PortfolioContentResponse content = PortfolioContentResponse.builder()
                .json(portfolio.getPortfolioContent().getContentJson())
                .html(portfolio.getPortfolioContent().getContentHtml())
                .build();

        return new PortfolioEditResponse(
                portfolio.getPrivateMemo(),
                portfolio.getPreviewSummary(),
                portfolio.getThumbnailImageId(),
                thumbnailImageUrl,
                jobCategories,
                portfolio.getCollaborationType(),
                portfolio.getVisibility(),
                portfolio.getTitle(),
                tags,
                toExternalLinkResponses(portfolio.getExternalLinks()),
                contentImages,
                content
        );
    }

    private List<PortfolioExternalLinkResponse> toExternalLinkResponses(List<ExternalLink> links) {
        if (links == null || links.isEmpty()) {
            return List.of();
        }
        return links.stream()
                .map(link -> new PortfolioExternalLinkResponse(link.label(), link.url()))
                .toList();
    }

    private List<PortfolioTagResponse> toTagResponses(List<PortfolioTag> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        return tags.stream()
                .map(tag -> new PortfolioTagResponse(tag.getUserInputTag(), tag.getSortOrder()))
                .toList();
    }

    private List<PortfolioJobCategoryResponse> toJobCategoriesResponse(PortfolioJobCategory portfolioJobCategory) {
        AssertionUtils.notNull(
                portfolioJobCategory,
                PortfolioApplicationExceptionCodeCluster.DetailResponse.JOB_CATEGORY_NOT_FOUND,
                PortfolioApplicationException::new
        );

        return loadJobCategoryPort.loadJobCategoryHierarchy(portfolioJobCategory.getJobCategoryId())
                .stream()
                .map(hierarchy -> new PortfolioJobCategoryResponse(
                        hierarchy.id(),
                        hierarchy.depth(),
                        hierarchy.categoryCode(),
                        hierarchy.name()
                ))
                .toList();
    }
}

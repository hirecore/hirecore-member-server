package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioDetailUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.*;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.ExistsPortfolioMemberInterestPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.ExistsPortfolioMemberViewPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioJobCategory;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadProfilePort;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LoadPortfolioDetailUseCaseImpl implements LoadPortfolioDetailUseCase {

    private final LoadPortfolioPort loadPortfolioPort;
    private final LoadProfilePort loadProfilePort;
    private final LoadJobCategoryPort loadJobCategoryPort;
    private final ExistsPortfolioMemberViewPort existsPortfolioMemberViewPort;
    private final ExistsPortfolioMemberInterestPort existsPortfolioMemberInterestPort;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional(readOnly = true)
    public PortfolioDetailResponse execute(Long portfolioId, Long viewerId) {
        Portfolio portfolio = loadPortfolio(portfolioId);
        boolean isOwner = portfolio.getMemberAccountId().equals(viewerId);
        ensureAccessible(portfolio, isOwner);

        String publisherNickname = loadProfilePort.findNickname(portfolio.getMemberAccountId())
                .orElseThrow(() -> new PortfolioApplicationException(
                        PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NICKNAME_NOT_FOUND
                ));

        long displayedViewCount = resolveDisplayedViewCount(portfolio, viewerId);
        Boolean isInterested = resolveIsInterested(portfolio, viewerId, isOwner);

        return buildResponse(
                portfolio,
                publisherNickname,
                isOwner,
                displayedViewCount,
                isInterested,
                toTagResponses(portfolio.getPortfolioTags()),
                toJobCategoriesResponse(portfolio.getPortfolioJobCategory())
        );
    }

    private Boolean resolveIsInterested(Portfolio portfolio, Long viewerId, boolean isOwner) {
        if (viewerId == null || isOwner) {
            return null;
        }
        return existsPortfolioMemberInterestPort.exists(portfolio.getId(), viewerId);
    }

    private long resolveDisplayedViewCount(Portfolio portfolio, Long viewerId) {
        long cachedViewCount = portfolio.getCachedViewCount();
        if (viewerId == null) {
            return cachedViewCount;
        }
        boolean alreadyViewed = existsPortfolioMemberViewPort.exists(portfolio.getId(), viewerId);
        boolean recorded = portfolio.markViewedBy(viewerId, alreadyViewed);
        if (!recorded) {
            return cachedViewCount;
        }
        portfolio.pollAllEvents().forEach(applicationEventPublisher::publishEvent);
        return cachedViewCount + 1;
    }

    private Portfolio loadPortfolio(Long portfolioId) {
        return loadPortfolioPort.findPortfolio(portfolioId)
                .orElseThrow(() -> new PortfolioApplicationException(
                        PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NOT_FOUND
                ));
    }

    private void ensureAccessible(Portfolio portfolio, boolean isOwner) {
        if (portfolio.getVisibility() != Visibility.PUBLIC && !isOwner) {
            throw new PortfolioApplicationException(
                    PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_FORBIDDEN
            );
        }
    }

    private PortfolioDetailResponse buildResponse(
            Portfolio portfolio,
            String publisherNickname,
            boolean isOwner,
            long displayedViewCount,
            Boolean isInterested,
            List<PortfolioTagResponse> tags,
            List<PortfolioJobCategoryResponse> jobCategories
    ) {
        PortfolioContentResponse content = PortfolioContentResponse.builder()
                .json(portfolio.getPortfolioContent().getContentJson())
                .html(portfolio.getPortfolioContent().getContentHtml())
                .build();

        return PortfolioDetailResponse.builder()
                .isOwner(isOwner)
                .publisher(publisherNickname)
                .jobCategories(jobCategories)
                .collaborationType(portfolio.getCollaborationType())
                .visibility(portfolio.getVisibility())
                .viewCount(displayedViewCount)
                .interestCount(portfolio.getCachedInterestCount())
                .isInterested(isInterested)
                .title(portfolio.getTitle())
                .content(content)
                .tags(tags)
                .externalLinks(toExternalLinkResponses(portfolio.getExternalLinks()))
                .updatedAt(resolveLatestUpdatedAt(portfolio))
                .build();
    }

    private Instant resolveLatestUpdatedAt(Portfolio portfolio) {
        Stream<Instant> portfolioAndContent = Stream.of(
                portfolio.getAuditingInfo().updatedAt(),
                portfolio.getPortfolioContent().getAuditingInfo().updatedAt()
        );
        Stream<Instant> tagUpdates = portfolio.getPortfolioTags() == null
                ? Stream.empty()
                : portfolio.getPortfolioTags().stream()
                        .map(tag -> tag.getAuditingInfo().updatedAt());
        return Stream.concat(portfolioAndContent, tagUpdates)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
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

    /*
     *   (@Parameter) PortfolioJobCategory = id, jobCategoryId, userInput, connectedAt
     *   (@Response) PortfolioJobCategoryResponse = id, depth, categoryCode, name
     * */
    private List<PortfolioJobCategoryResponse> toJobCategoriesResponse(PortfolioJobCategory portfolioJobCategory) {
        // JOB_CATEGORY의 NOT_NULL 보장
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

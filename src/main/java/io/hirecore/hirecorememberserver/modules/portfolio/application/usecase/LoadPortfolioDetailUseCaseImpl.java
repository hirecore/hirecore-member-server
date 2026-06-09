package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioDetailUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.ExistsPortfolioMemberInterestPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.ExistsPortfolioMemberViewPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfoliosByMemberPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioJobCategory;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedResponseDto;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadCoverLetterContentPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadProfilePort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadResumeContentPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.PublishDomainEventsPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.CoverLetterContentResult;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.PortfolioJobCategoryHierarchyResult;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.ResumeContentResult;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LoadPortfolioDetailUseCaseImpl implements LoadPortfolioDetailUseCase {

    private final LoadPortfolioPort loadPortfolioPort;
    private final LoadPortfoliosByMemberPort loadPortfoliosByMemberPort;
    private final LoadProfilePort loadProfilePort;
    private final LoadJobCategoryPort loadJobCategoryPort;
    private final LoadResumeContentPort loadResumeContentPort;
    private final LoadCoverLetterContentPort loadCoverLetterContentPort;
    private final ExistsPortfolioMemberViewPort existsPortfolioMemberViewPort;
    private final ExistsPortfolioMemberInterestPort existsPortfolioMemberInterestPort;
    private final PublishDomainEventsPort publishDomainEventsPort;

    @Override
    @Transactional(readOnly = true)
    public Response execute(Long portfolioId, Long viewerId) {
        Portfolio portfolio = loadPortfolio(portfolioId);
        boolean isOwner = portfolio.getMemberAccountId().equals(viewerId);
        ensureAccessible(portfolio, isOwner);

        String publisherNickname = loadProfilePort.findNickname(portfolio.getMemberAccountId())
                .orElseThrow(() -> new PortfolioApplicationException(
                        PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NICKNAME_NOT_FOUND
                ));

        long displayedViewCount = resolveDisplayedViewCount(portfolio, viewerId);
        Boolean isInterested = resolveIsInterested(portfolio, viewerId, isOwner);

        return Response.builder()
                .isOwner(isOwner)
                .viewCount(displayedViewCount)
                .interestCount(portfolio.getCachedInterestCount())
                .isInterested(isInterested)
                .updatedAt(resolveLatestUpdatedAt(portfolio))
                .portfolio(buildPortfolioBody(portfolio))
                .publisher(buildPublisher(portfolio, publisherNickname))
                .linkedResume(buildLinkedResume(portfolio.getResumeId(), viewerId))
                .linkedCoverLetter(buildLinkedCoverLetter(portfolio.getCoverLetterId(), viewerId))
                .build();
    }

    /**
     * publisher 섹션을 합성한다. otherPortfolios 는 작성자의 PUBLIC 작품 중 본 포트폴리오를 제외한 전체를
     * 마지막 수정 시각 내림차순으로 노출한다. 작품이 없으면 빈 배열로 응답한다.
     *
     * <p>각 작품의 직무 카테고리 계층은 leaf id 들을 모아 단일 쿼리 (Recursive CTE) 로 일괄 로딩하므로
     * 작품 개수 K 와 무관하게 SQL 한 번만 발행된다.</p>
     */
    private Response.Publisher buildPublisher(Portfolio portfolio, String publisherNickname) {
        List<Portfolio> otherPublicPortfolios = loadPortfoliosByMemberPort
                .findAllPublicByMemberAccountIdExcludingOrderByUpdatedAtDesc(
                        portfolio.getMemberAccountId(),
                        portfolio.getId()
                );
        List<Long> otherLeafIds = otherPublicPortfolios.stream()
                .map(other -> {
                    PortfolioJobCategory pjc = other.getPortfolioJobCategory();
                    AssertionUtils.notNull(
                            pjc,
                            PortfolioApplicationExceptionCodeCluster.DetailResponse.JOB_CATEGORY_NOT_FOUND,
                            PortfolioApplicationException::new
                    );
                    return pjc.getJobCategoryId();
                })
                .toList();
        Map<Long, List<PortfolioJobCategoryHierarchyResult>> hierarchiesByLeafId =
                loadJobCategoryPort.loadJobCategoryHierarchies(otherLeafIds);

        List<Response.Publisher.OtherPortfolioSummary> otherPortfolios = otherPublicPortfolios.stream()
                .map(other -> toOtherPortfolioSummary(other, hierarchiesByLeafId))
                .toList();
        return new Response.Publisher(publisherNickname, otherPortfolios);
    }

    private Response.Publisher.OtherPortfolioSummary toOtherPortfolioSummary(
            Portfolio other,
            Map<Long, List<PortfolioJobCategoryHierarchyResult>> hierarchiesByLeafId
    ) {
        Long leafId = other.getPortfolioJobCategory().getJobCategoryId();
        List<SharedResponseDto.JobCategory> jobCategories = hierarchiesByLeafId
                .getOrDefault(leafId, List.of()).stream()
                .map(h -> new SharedResponseDto.JobCategory(h.id(), h.depth(), h.categoryCode(), h.name()))
                .toList();
        return new Response.Publisher.OtherPortfolioSummary(
                other.getId(),
                other.getTitle(),
                jobCategories,
                other.getCachedViewCount(),
                other.getCachedInterestCount(),
                other.getAuditingInfo().updatedAt()
        );
    }

    private Response.Body buildPortfolioBody(Portfolio portfolio) {
        return new Response.Body(
                portfolio.getTitle(),
                portfolio.getCollaborationType(),
                portfolio.getVisibility(),
                toJobCategoriesResponse(portfolio.getPortfolioJobCategory()),
                toTagResponses(portfolio.getPortfolioTags()),
                toExternalLinkResponses(portfolio.getExternalLinks()),
                new SharedResponseDto.RichTextContent(
                        portfolio.getPortfolioContent().getContentJson(),
                        portfolio.getPortfolioContent().getContentHtml()
                )
        );
    }

    /**
     * 자원 자체의 가시성 정책을 기준으로 본문 노출 여부를 결정한다.
     *
     * <p>자원이 존재하지 않거나 연결이 없으면 {@code null} 을 반환한다.
     * 자원이 PUBLIC 이거나 viewer 가 자원 소유자와 일치하면 본문을 채우고,
     * 그 외에는 {@code content} 를 {@code null} 로 두어 메타만 노출한다.</p>
     */
    private Response.LinkedResume buildLinkedResume(Long resumeId, Long viewerId) {
        if (resumeId == null) {
            return null;
        }
        ResumeContentResult result = loadResumeContentPort.findById(resumeId).orElse(null);
        if (result == null) {
            return null;
        }
        SharedResponseDto.RichTextContent content = canViewContent(result.visibility(), result.memberAccountId(), viewerId)
                ? new SharedResponseDto.RichTextContent(result.contentJson(), result.contentHtml())
                : null;
        return new Response.LinkedResume(result.id(), result.title(), content);
    }

    private Response.LinkedCoverLetter buildLinkedCoverLetter(Long coverLetterId, Long viewerId) {
        if (coverLetterId == null) {
            return null;
        }
        CoverLetterContentResult result = loadCoverLetterContentPort.findById(coverLetterId).orElse(null);
        if (result == null) {
            return null;
        }
        SharedResponseDto.RichTextContent content = canViewContent(result.visibility(), result.memberAccountId(), viewerId)
                ? new SharedResponseDto.RichTextContent(result.contentJson(), result.contentHtml())
                : null;
        return new Response.LinkedCoverLetter(result.id(), result.title(), content);
    }

    private static boolean canViewContent(Visibility resourceVisibility, Long resourceOwnerId, Long viewerId) {
        if (resourceVisibility == Visibility.PUBLIC) {
            return true;
        }
        return viewerId != null && viewerId.equals(resourceOwnerId);
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
        publishDomainEventsPort.publishAll(portfolio);
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

        return loadJobCategoryPort.loadJobCategoryHierarchy(portfolioJobCategory.getJobCategoryId())
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

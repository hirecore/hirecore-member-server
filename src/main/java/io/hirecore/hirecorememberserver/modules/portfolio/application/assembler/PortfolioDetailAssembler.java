package io.hirecore.hirecorememberserver.modules.portfolio.application.assembler;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioDetailUseCase.Response;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioJobCategory;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedResponseDto;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadCoverLetterContentPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadResumeContentPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.CoverLetterContentResult;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.PortfolioJobCategoryHierarchyResult;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.ResumeContentResult;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

// 포트폴리오 상세 응답의 섹션(body·publisher·연결 자원)을 out-port 조회로 조립
@Component
@RequiredArgsConstructor
public class PortfolioDetailAssembler {

    private final LoadPortfolioPort loadPortfolioPort;
    private final LoadJobCategoryPort loadJobCategoryPort;
    private final LoadResumeContentPort loadResumeContentPort;
    private final LoadCoverLetterContentPort loadCoverLetterContentPort;

    public Response.Body buildBody(Portfolio portfolio) {
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

    // publisher 섹션 합성 (작성자의 다른 PUBLIC 작품, 직무 계층은 배치 단일 쿼리)
    public Response.Publisher buildPublisher(Portfolio portfolio, String publisherNickname) {
        List<Portfolio> otherPublicPortfolios = loadPortfolioPort
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
                    return pjc.getLeafJobCategoryId();
                })
                .toList();
        Map<Long, List<PortfolioJobCategoryHierarchyResult>> hierarchiesByLeafId =
                loadJobCategoryPort.findJobCategoryHierarchies(otherLeafIds);

        List<Response.Publisher.OtherPortfolioSummary> otherPortfolios = otherPublicPortfolios.stream()
                .map(other -> toOtherPortfolioSummary(other, hierarchiesByLeafId))
                .toList();
        return new Response.Publisher(publisherNickname, otherPortfolios);
    }

    // 자원 가시성에 따라 본문 노출 결정 (PUBLIC이거나 소유자면 본문, 아니면 메타만)
    public Response.LinkedResume buildLinkedResume(Long resumeId, Long viewerId) {
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

    public Response.LinkedCoverLetter buildLinkedCoverLetter(Long coverLetterId, Long viewerId) {
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

    private Response.Publisher.OtherPortfolioSummary toOtherPortfolioSummary(
            Portfolio other,
            Map<Long, List<PortfolioJobCategoryHierarchyResult>> hierarchiesByLeafId
    ) {
        Long leafId = other.getPortfolioJobCategory().getLeafJobCategoryId();
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

    private static boolean canViewContent(Visibility resourceVisibility, Long resourceOwnerId, Long viewerId) {
        if (resourceVisibility.isPublic()) {
            return true;
        }
        return viewerId != null && viewerId.equals(resourceOwnerId);
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

package io.hirecore.hirecorememberserver.modules.portfolio.application.assembler;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPublicPortfolioSummariesUseCase.Response;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.FindInterestedPortfolioIdsPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPublicPortfolioSummaryPort.PublicPortfolioRow;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioJobCategory;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedResponseDto;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadImageUrlSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategorySharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadProfileNicknameSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// 공개 포트폴리오 목록 응답 조립 (직무 계층 배치 조회, 닉네임/썸네일 조회, viewer 기준 isOwner 판정)
@Component
@RequiredArgsConstructor
public class PublicPortfolioSummariesAssembler {

    private final LoadJobCategorySharedPort loadJobCategorySharedPort;
    private final LoadProfileNicknameSharedPort loadProfileNicknameSharedPort;
    private final LoadImageUrlSharedPort loadImageUrlSharedPort;
    private final FindInterestedPortfolioIdsPort findInterestedPortfolioIdsPort;

    public List<Response.Item> buildItems(List<PublicPortfolioRow> pageRows, Long viewerId) {
        Map<Long, List<LoadJobCategorySharedPort.Result>> hierarchiesByLeafId =
                loadJobCategorySharedPort.findJobCategoryHierarchies(collectLeafIds(pageRows));

        // 로그인 사용자에 한해, 이 페이지에서 관심 등록한 포트폴리오 ID를 배치로 한 번에 조회 (N+1 회피)
        Set<Long> interestedPortfolioIds = (viewerId == null)
                ? Set.of()
                : findInterestedPortfolioIdsPort.findInterestedPortfolioIds(collectPortfolioIds(pageRows), viewerId);

        return pageRows.stream()
                .map(row -> toItem(row, hierarchiesByLeafId, interestedPortfolioIds, viewerId))
                .toList();
    }

    private Response.Item toItem(
            PublicPortfolioRow row,
            Map<Long, List<LoadJobCategorySharedPort.Result>> hierarchiesByLeafId,
            Set<Long> interestedPortfolioIds,
            Long viewerId
    ) {
        Portfolio portfolio = row.portfolio();
        Long leafId = portfolio.getPortfolioJobCategory().getLeafJobCategoryId();
        List<SharedResponseDto.JobCategory> jobCategories = hierarchiesByLeafId
                .getOrDefault(leafId, List.of()).stream()
                .map(h -> new SharedResponseDto.JobCategory(h.id(), h.depth(), h.categoryCode(), h.name()))
                .toList();

        String nickname = loadProfileNicknameSharedPort.findNickname(portfolio.getMemberAccountId()).orElse(null);
        boolean isOwner = portfolio.isOwnedBy(viewerId);
        Boolean isInterested = resolveIsInterested(viewerId, isOwner, portfolio.getId(), interestedPortfolioIds);

        return new Response.Item(
                portfolio.getId(),
                buildThumbnail(portfolio.getThumbnailImageId()),
                jobCategories,
                portfolio.getTitle(),
                portfolio.getPreviewSummary(),
                portfolio.getCollaborationType(),
                toTagResponses(portfolio.getPortfolioTags()),
                toExternalLinkResponses(portfolio.getExternalLinks()),
                nickname,
                portfolio.getCachedViewCount(),
                portfolio.getCachedInterestCount(),
                isOwner,
                isInterested,
                row.effectiveUpdatedAt()
        );
    }

    // 비로그인 또는 본인 글이면 관심 여부는 의미 없음(null). 그 외에는 배치 조회 집합 포함 여부.
    private static Boolean resolveIsInterested(
            Long viewerId,
            boolean isOwner,
            Long portfolioId,
            Set<Long> interestedPortfolioIds
    ) {
        if (viewerId == null || isOwner) {
            return null;
        }
        return interestedPortfolioIds.contains(portfolioId);
    }

    private static Set<Long> collectPortfolioIds(List<PublicPortfolioRow> rows) {
        return rows.stream()
                .map(row -> row.portfolio().getId())
                .collect(Collectors.toSet());
    }

    private SharedResponseDto.Thumbnail buildThumbnail(Long thumbnailImageId) {
        if (thumbnailImageId == null) {
            return null;
        }
        String imageUrl = loadImageUrlSharedPort.findUrlById(thumbnailImageId).orElse(null);
        return new SharedResponseDto.Thumbnail(thumbnailImageId, imageUrl);
    }

    private static Set<Long> collectLeafIds(List<PublicPortfolioRow> rows) {
        Set<Long> leafIds = new HashSet<>();
        for (PublicPortfolioRow row : rows) {
            PortfolioJobCategory pjc = row.portfolio().getPortfolioJobCategory();
            if (pjc != null && pjc.getLeafJobCategoryId() != null) {
                leafIds.add(pjc.getLeafJobCategoryId());
            }
        }
        return leafIds;
    }

    private static List<SharedResponseDto.SequentialTag> toTagResponses(List<PortfolioTag> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        return tags.stream()
                .map(tag -> new SharedResponseDto.SequentialTag(tag.getName(), tag.getSortOrder()))
                .toList();
    }

    private static List<SharedResponseDto.ExternalLink> toExternalLinkResponses(List<ExternalLink> links) {
        if (links == null || links.isEmpty()) {
            return List.of();
        }
        return links.stream()
                .map(link -> new SharedResponseDto.ExternalLink(link.label(), link.url()))
                .toList();
    }
}

package io.hirecore.hirecorememberserver.modules.portfolio.application.assembler;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPublicPortfolioSummariesUseCase.Response;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPublicPortfolioSummaryPort.PublicPortfolioRow;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioJobCategory;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedResponseDto;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadImageUrlSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategorySharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadProfileNicknameSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.PortfolioJobCategoryHierarchyResult;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// 공개 포트폴리오 목록 응답 조립 (직무 계층 배치 조회, 닉네임/썸네일 조회, viewer 기준 isOwner 판정)
@Component
@RequiredArgsConstructor
public class PublicPortfolioSummariesAssembler {

    private final LoadJobCategorySharedPort loadJobCategoryPort;
    private final LoadProfileNicknameSharedPort loadProfileNicknamePort;
    private final LoadImageUrlSharedPort loadImageUrlPort;

    public List<Response.Item> buildItems(List<PublicPortfolioRow> pageRows, Long viewerId) {
        Map<Long, List<PortfolioJobCategoryHierarchyResult>> hierarchiesByLeafId =
                loadJobCategoryPort.findJobCategoryHierarchies(collectLeafIds(pageRows));

        return pageRows.stream()
                .map(row -> toItem(row, hierarchiesByLeafId, viewerId))
                .toList();
    }

    private Response.Item toItem(
            PublicPortfolioRow row,
            Map<Long, List<PortfolioJobCategoryHierarchyResult>> hierarchiesByLeafId,
            Long viewerId
    ) {
        Portfolio portfolio = row.portfolio();
        Long leafId = portfolio.getPortfolioJobCategory().getLeafJobCategoryId();
        List<SharedResponseDto.JobCategory> jobCategories = hierarchiesByLeafId
                .getOrDefault(leafId, List.of()).stream()
                .map(h -> new SharedResponseDto.JobCategory(h.id(), h.depth(), h.categoryCode(), h.name()))
                .toList();

        String nickname = loadProfileNicknamePort.findNickname(portfolio.getMemberAccountId()).orElse(null);
        boolean isOwner = portfolio.isOwnedBy(viewerId);

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
                row.effectiveUpdatedAt()
        );
    }

    private SharedResponseDto.Thumbnail buildThumbnail(Long thumbnailImageId) {
        if (thumbnailImageId == null) {
            return null;
        }
        String imageUrl = loadImageUrlPort.findUrlById(thumbnailImageId).orElse(null);
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

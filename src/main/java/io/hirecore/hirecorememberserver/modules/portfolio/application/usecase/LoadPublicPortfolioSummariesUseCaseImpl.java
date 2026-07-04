package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPublicPortfolioSummariesUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPublicPortfolioSummaryPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.cursor.EffectiveTimeCursor;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPublicPortfolioSummaryPort.PublicPortfolioRow;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioJobCategory;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedResponseDto;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadImageUrlPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadProfileNicknamePort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.PortfolioJobCategoryHierarchyResult;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LoadPublicPortfolioSummariesUseCaseImpl implements LoadPublicPortfolioSummariesUseCase {

    private final LoadPublicPortfolioSummaryPort loadPublicPortfolioSummaryPort;
    private final LoadJobCategoryPort loadJobCategoryPort;
    private final LoadProfileNicknamePort loadProfileNicknamePort;
    private final LoadImageUrlPort loadImageUrlPort;

    // 커서 페이징(size+1로 hasNext 판정) 후 cross-BC 메타 합성 및 viewer 기준 isOwner 판정
    @Override
    @Transactional(readOnly = true)
    public Response execute(String cursorToken, int size, Long viewerId) {
        EffectiveTimeCursor cursor = (cursorToken == null || cursorToken.isBlank())
                ? null
                : EffectiveTimeCursor.decode(cursorToken);

        List<PublicPortfolioRow> rows = loadPublicPortfolioSummaryPort
                .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(
                        cursor != null ? cursor.time() : null,
                        cursor != null ? cursor.id() : null,
                        size + 1
                );

        boolean hasNext = rows.size() > size;
        List<PublicPortfolioRow> pageRows = hasNext ? rows.subList(0, size) : rows;

        Map<Long, List<PortfolioJobCategoryHierarchyResult>> hierarchiesByLeafId =
                loadJobCategoryPort.findJobCategoryHierarchies(collectLeafIds(pageRows));

        List<Response.Item> items = pageRows.stream()
                .map(row -> toItem(row, hierarchiesByLeafId, viewerId))
                .toList();

        String nextCursor = hasNext ? encodeNextCursor(pageRows) : null;
        Response.Pagination pagination = new Response.Pagination(nextCursor, hasNext);
        return new Response(items, pagination);
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

    private static String encodeNextCursor(List<PublicPortfolioRow> pageRows) {
        PublicPortfolioRow last = pageRows.get(pageRows.size() - 1);
        return new EffectiveTimeCursor(
                last.effectiveUpdatedAt(),
                last.portfolio().getId()
        ).encode();
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

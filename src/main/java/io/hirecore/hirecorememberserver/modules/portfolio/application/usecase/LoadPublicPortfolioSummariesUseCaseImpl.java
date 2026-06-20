package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPublicPortfolioSummariesUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPublicPortfolioSummariesPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.cursor.EffectiveTimeCursor;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPublicPortfolioSummariesPort.PublicPortfolioRow;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioJobCategory;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedResponseDto;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadImageUrlPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadProfilePort;
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

    private final LoadPublicPortfolioSummariesPort loadPublicPortfolioSummariesPort;
    private final LoadJobCategoryPort loadJobCategoryPort;
    private final LoadProfilePort loadProfilePort;
    private final LoadImageUrlPort loadImageUrlPort;

    /**
     *  [로직 플로우]
     *   1. cursor 디코드 (없으면 첫 페이지)
     *   2. port 호출 — limit = size + 1 (hasNext 판정용 한 개 더)
     *   3. hasNext 판정 후 size 개로 트림
     *   4. 응답에 필요한 cross-BC 메타(닉네임, 썸네일 URL, 직무 계층) 합성 + viewer 기준 isOwner 판정
     *      - 직무 계층은 leaf id 들을 모아 단일 배치 조회
     *      - 닉네임/썸네일 URL 은 현재 단건 호출 (LoadMyPortfolioSummaries 와 동일 패턴).
     *        size 가 작아 비용은 제한적이나, 트래픽 증가 시 일괄 조회 port 추가 검토 여지.
     *      - isOwner: viewerId 가 null (비로그인) 이면 항상 false. {@code Long.equals(null) == false} 로 자연 처리.
     *   5. nextCursor 인코드 (hasNext = true 인 경우에만)
     */
    @Override
    @Transactional(readOnly = true)
    public Response execute(String cursorToken, int size, Long viewerId) {
        EffectiveTimeCursor cursor = (cursorToken == null || cursorToken.isBlank())
                ? null
                : EffectiveTimeCursor.decode(cursorToken);

        List<PublicPortfolioRow> rows = loadPublicPortfolioSummariesPort
                .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(
                        cursor != null ? cursor.time() : null,
                        cursor != null ? cursor.id() : null,
                        size + 1
                );

        boolean hasNext = rows.size() > size;
        List<PublicPortfolioRow> pageRows = hasNext ? rows.subList(0, size) : rows;

        Map<Long, List<PortfolioJobCategoryHierarchyResult>> hierarchiesByLeafId =
                loadJobCategoryPort.loadJobCategoryHierarchies(collectLeafIds(pageRows));

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

        String nickname = loadProfilePort.findNickname(portfolio.getMemberAccountId()).orElse(null);
        boolean isOwner = portfolio.getMemberAccountId().equals(viewerId);

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

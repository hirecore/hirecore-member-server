package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadMyPortfolioSummariesUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioJobCategory;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedResponseDto;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadCoverLetterTitlePort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadImageUrlPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadResumeTitlePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LoadMyPortfolioSummariesUseCaseImpl implements LoadMyPortfolioSummariesUseCase {

    private final LoadPortfolioPort loadPortfolioPort;
    private final LoadResumeTitlePort loadResumeTitlePort;
    private final LoadCoverLetterTitlePort loadCoverLetterTitlePort;
    private final LoadJobCategoryPort loadJobCategoryPort;
    private final LoadImageUrlPort loadImageUrlPort;

    // 본인 포트폴리오 목록 조회 후 연결 자원 제목 bulk 조회로 합성
    @Override
    @Transactional(readOnly = true)
    public Response execute(Long viewerId) {
        List<Portfolio> portfolios = loadPortfolioPort
                .findAllByMemberAccountIdOrderByUpdatedAtDesc(viewerId);
        if (portfolios.isEmpty()) {
            return new Response(List.of());
        }

        Map<Long, String> resumeTitles = loadResumeTitlePort
                .findTitleMapByIds(collectIds(portfolios, Portfolio::getResumeId));
        Map<Long, String> coverLetterTitles = loadCoverLetterTitlePort
                .findTitleMapByIds(collectIds(portfolios, Portfolio::getCoverLetterId));

        List<Response.Item> items = portfolios.stream()
                .map(p -> toItem(p, resumeTitles, coverLetterTitles))
                .toList();
        return new Response(items);
    }

    private Response.Item toItem(
            Portfolio portfolio,
            Map<Long, String> resumeTitles,
            Map<Long, String> coverLetterTitles
    ) {
        return new Response.Item(
                portfolio.getId(),
                portfolio.getTitle(),
                portfolio.getPreviewSummary(),
                portfolio.getPrivateMemo(),
                buildThumbnail(portfolio.getThumbnailImageId()),
                toJobCategoriesResponse(portfolio.getPortfolioJobCategory()),
                portfolio.getCollaborationType(),
                portfolio.getVisibility(),
                toTagResponses(portfolio.getPortfolioTags()),
                portfolio.getCachedInterestCount(),
                toLinkedResume(portfolio.getResumeId(), resumeTitles),
                toLinkedCoverLetter(portfolio.getCoverLetterId(), coverLetterTitles),
                portfolio.getAuditingInfo().updatedAt()
        );
    }

    // 썸네일 없으면 null, URL 해소 실패 시 imageId만 채움
    private SharedResponseDto.Thumbnail buildThumbnail(Long thumbnailImageId) {
        if (thumbnailImageId == null) {
            return null;
        }
        String imageUrl = loadImageUrlPort.findUrlById(thumbnailImageId).orElse(null);
        return new SharedResponseDto.Thumbnail(thumbnailImageId, imageUrl);
    }

    private static Set<Long> collectIds(List<Portfolio> portfolios, java.util.function.Function<Portfolio, Long> idGetter) {
        Set<Long> ids = new HashSet<>();
        for (Portfolio portfolio : portfolios) {
            Long id = idGetter.apply(portfolio);
            if (id != null) {
                ids.add(id);
            }
        }
        return ids;
    }

    private static Response.LinkedResume toLinkedResume(Long resumeId, Map<Long, String> resumeTitles) {
        if (resumeId == null) {
            return null;
        }
        String title = resumeTitles.get(resumeId);
        if (title == null) {
            return null;
        }
        return new Response.LinkedResume(resumeId, title);
    }

    private static Response.LinkedCoverLetter toLinkedCoverLetter(Long coverLetterId, Map<Long, String> coverLetterTitles) {
        if (coverLetterId == null) {
            return null;
        }
        String title = coverLetterTitles.get(coverLetterId);
        if (title == null) {
            return null;
        }
        return new Response.LinkedCoverLetter(coverLetterId, title);
    }

    private static List<SharedResponseDto.SequentialTag> toTagResponses(List<PortfolioTag> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        return tags.stream()
                .map(tag -> new SharedResponseDto.SequentialTag(tag.getName(), tag.getSortOrder()))
                .toList();
    }

    private List<SharedResponseDto.JobCategory> toJobCategoriesResponse(PortfolioJobCategory portfolioJobCategory) {
        if (portfolioJobCategory == null) {
            return List.of();
        }
        return loadJobCategoryPort.findJobCategoryHierarchy(portfolioJobCategory.getLeafJobCategoryId())
                .stream()
                .filter(Objects::nonNull)
                .map(hierarchy -> new SharedResponseDto.JobCategory(
                        hierarchy.id(),
                        hierarchy.depth(),
                        hierarchy.categoryCode(),
                        hierarchy.name()
                ))
                .toList();
    }
}

package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadMyPortfolioSummariesUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.LinkedCoverLetterResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.LinkedResumeResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.MyPortfolioSummariesResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.MyPortfolioSummaryItemResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioJobCategoryResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioTagResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfoliosByMemberPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioJobCategory;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadCoverLetterTitlesPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadImageUrlPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadResumeTitlesPort;
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

    private final LoadPortfoliosByMemberPort loadPortfoliosByMemberPort;
    private final LoadResumeTitlesPort loadResumeTitlesPort;
    private final LoadCoverLetterTitlesPort loadCoverLetterTitlesPort;
    private final LoadJobCategoryPort loadJobCategoryPort;
    private final LoadImageUrlPort loadImageUrlPort;

    /**
     *  [로직 플로우]
     *   1. 본인이 작성한 모든 포트폴리오를 updatedAt DESC 로 적재
     *   2. linkedResumeId / linkedCoverLetterId 를 모아 sharedkernel port 로 제목을 bulk 조회 (각 1쿼리)
     *   3. 포트폴리오별로 썸네일 URL, 직군 계층, 태그, 연결 자원 제목을 합쳐 응답 DTO 로 변환
     */
    @Override
    @Transactional(readOnly = true)
    public MyPortfolioSummariesResponse execute(Long viewerId) {
        List<Portfolio> portfolios = loadPortfoliosByMemberPort
                .findAllByMemberAccountIdOrderByUpdatedAtDesc(viewerId);
        if (portfolios.isEmpty()) {
            return new MyPortfolioSummariesResponse(List.of());
        }

        Map<Long, String> resumeTitles = loadResumeTitlesPort
                .findAllTitlesByIds(collectIds(portfolios, Portfolio::getResumeId));
        Map<Long, String> coverLetterTitles = loadCoverLetterTitlesPort
                .findAllTitlesByIds(collectIds(portfolios, Portfolio::getCoverLetterId));

        List<MyPortfolioSummaryItemResponse> items = portfolios.stream()
                .map(p -> toItem(p, resumeTitles, coverLetterTitles))
                .toList();
        return new MyPortfolioSummariesResponse(items);
    }

    private MyPortfolioSummaryItemResponse toItem(
            Portfolio portfolio,
            Map<Long, String> resumeTitles,
            Map<Long, String> coverLetterTitles
    ) {
        String thumbnailImageUrl = portfolio.getThumbnailImageId() == null
                ? null
                : loadImageUrlPort.findUrlById(portfolio.getThumbnailImageId()).orElse(null);

        return new MyPortfolioSummaryItemResponse(
                portfolio.getId(),
                portfolio.getTitle(),
                portfolio.getPreviewSummary(),
                portfolio.getPrivateMemo(),
                portfolio.getThumbnailImageId(),
                thumbnailImageUrl,
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

    private static LinkedResumeResponse toLinkedResume(Long resumeId, Map<Long, String> resumeTitles) {
        if (resumeId == null) {
            return null;
        }
        String title = resumeTitles.get(resumeId);
        if (title == null) {
            return null;
        }
        return new LinkedResumeResponse(resumeId, title);
    }

    private static LinkedCoverLetterResponse toLinkedCoverLetter(Long coverLetterId, Map<Long, String> coverLetterTitles) {
        if (coverLetterId == null) {
            return null;
        }
        String title = coverLetterTitles.get(coverLetterId);
        if (title == null) {
            return null;
        }
        return new LinkedCoverLetterResponse(coverLetterId, title);
    }

    private static List<PortfolioTagResponse> toTagResponses(List<PortfolioTag> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        return tags.stream()
                .map(tag -> new PortfolioTagResponse(tag.getName(), tag.getSortOrder()))
                .toList();
    }

    private List<PortfolioJobCategoryResponse> toJobCategoriesResponse(PortfolioJobCategory portfolioJobCategory) {
        if (portfolioJobCategory == null) {
            return List.of();
        }
        return loadJobCategoryPort.loadJobCategoryHierarchy(portfolioJobCategory.getJobCategoryId())
                .stream()
                .filter(Objects::nonNull)
                .map(hierarchy -> new PortfolioJobCategoryResponse(
                        hierarchy.id(),
                        hierarchy.depth(),
                        hierarchy.categoryCode(),
                        hierarchy.name()
                ))
                .toList();
    }
}

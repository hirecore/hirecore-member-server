package io.hirecore.hirecorememberserver.modules.portfolio.application.assembler;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadMyPortfolioSummariesUseCase.Response;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioJobCategory;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedResponseDto;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadCoverLetterTitleSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadImageUrlSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategorySharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadResumeTitleSharedPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

// 본인 포트폴리오 목록 응답 조립 (연결 자원 제목은 bulk 조회, 썸네일/직무 계층은 항목별 조회)
@Component
@RequiredArgsConstructor
public class MyPortfolioSummariesAssembler {

    private final LoadResumeTitleSharedPort loadResumeTitlePort;
    private final LoadCoverLetterTitleSharedPort loadCoverLetterTitlePort;
    private final LoadJobCategorySharedPort loadJobCategoryPort;
    private final LoadImageUrlSharedPort loadImageUrlPort;

    public List<Response.Item> buildItems(List<Portfolio> portfolios) {
        Map<Long, String> resumeTitles = loadResumeTitlePort
                .findTitleMapByIds(collectIds(portfolios, Portfolio::getResumeId));
        Map<Long, String> coverLetterTitles = loadCoverLetterTitlePort
                .findTitleMapByIds(collectIds(portfolios, Portfolio::getCoverLetterId));

        return portfolios.stream()
                .map(p -> toItem(p, resumeTitles, coverLetterTitles))
                .toList();
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

    private static Set<Long> collectIds(List<Portfolio> portfolios, Function<Portfolio, Long> idGetter) {
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

package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.CreatePortfolioUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.CreatePortfolioCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.PortfolioContentCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.SavePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryIdByCodePort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.UpdateUploadStatusOfImageFileMetaPort;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.HtmlPreviewSummarizer;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreatePortfolioUseCaseImpl implements CreatePortfolioUseCase {

    private final UpdateUploadStatusOfImageFileMetaPort updateUploadStatusOfImageFileMetaPort;
    private final LoadJobCategoryIdByCodePort loadJobCategoryIdByCodePort;
    private final SavePortfolioPort savePortfolioPort;
    private final ObjectMapper objectMapper;

    /**
     *  [기능 개발 항목]
     *    1. ImageFileMeta의 상태 변경 (소유권 검증 + UPLOADED 전이 + ImageUploadedEvent 발행)
     *    2. Portfolio aggregate 데이터 삽입
     *
     *  [로직 플로우]
     *    1. 이미지 파일 메타 상태값의 변경
     *    2. 카테고리 코드 해석
     *    3. previewSummary / portfolioTags / externalLinks 가공 (도메인은 가공된 값을 그대로 받음)
     *    4. 포트폴리오 데이터 삽입
     *
     *  [부수효과]
     *    UserStorageUsage 갱신은 ImageUploadedEvent 핸들러(storage BC)가 트랜잭션 커밋 이후
     *    비동기로 처리합니다. 본 UseCase는 동기적으로 직접 호출하지 않습니다 (ADR #172 Phase 3).
     * */
    @Override
    @Transactional
    public Long execute(Long memberId, CreatePortfolioCommand command) {
        List<Long> imageIds = aggregateImageIds(command.thumbnailImageId(), command.contentImageIds());

        updateUploadStatusOfImageFileMetaPort.markUploaded(memberId, imageIds);

        Long jobCategoryId = loadJobCategoryIdByCodePort.findIdByCode(command.categoryCode());
        Portfolio portfolio = buildPortfolio(memberId, command, jobCategoryId);

        return savePortfolioPort.save(portfolio).getId();
    }

    private Portfolio buildPortfolio(Long memberId, CreatePortfolioCommand command, Long jobCategoryId) {
        PortfolioContentCommand content = command.content();
        String previewSummary = resolvePreviewSummary(content.html(), command.title());
        List<PortfolioTag> portfolioTags = toPortfolioTags(command.tags());
        List<ExternalLink> externalLinks = command.externalLinks() != null ? command.externalLinks() : List.of();

        return Portfolio.create(
                memberId,
                jobCategoryId,
                command.customCategory(),
                command.thumbnailImageId(),
                command.linkedCoverLetterId(),
                command.linkedResumeId(),
                command.title(),
                previewSummary,
                serializeContentJson(content),
                content.html(),
                externalLinks,
                command.privateMemo(),
                portfolioTags,
                command.collaborationType(),
                command.visibility()
        );
    }

    /**
     * 본문 HTML 로부터 미리보기 요약을 산출합니다.
     * 본문에 텍스트가 없어 빈 문자열이 산출되면(이미지 위주 포트폴리오 등) 제목으로 fallback 합니다.
     */
    private static String resolvePreviewSummary(String contentHtml, String title) {
        String summary = HtmlPreviewSummarizer.summarize(contentHtml, Portfolio.PREVIEW_SUMMARY_MAX_LENGTH);
        return summary.isBlank() ? title : summary;
    }

    private static List<PortfolioTag> toPortfolioTags(List<String> userInputTags) {
        if (userInputTags == null || userInputTags.isEmpty()) {
            return List.of();
        }
        return userInputTags.stream()
                .map(PortfolioTag::create)
                .toList();
    }

    private String serializeContentJson(PortfolioContentCommand content) {
        try {
            return objectMapper.writeValueAsString(content.json());
        } catch (JsonProcessingException e) {
            throw new PortfolioApplicationException(
                    PortfolioApplicationExceptionCodeCluster.DetailResponse.CONTENT_JSON_SERIALIZATION_FAILED
            );
        }
    }

    private static List<Long> aggregateImageIds(Long thumbnailImageId, List<Long> contentImageIds) {
        List<Long> imageIds = new ArrayList<>();

        if (thumbnailImageId != null) {
            imageIds.add(thumbnailImageId);
        }

        if (contentImageIds != null && !contentImageIds.isEmpty()) {
            imageIds.addAll(contentImageIds);
        }

        return imageIds;
    }
}

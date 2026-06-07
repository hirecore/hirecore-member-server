package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.CreatePortfolioUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.SavePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedCommandDto;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.MarkImagesAsUploadedPort;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreatePortfolioUseCaseImpl implements CreatePortfolioUseCase {

    private final MarkImagesAsUploadedPort markImagesAsUploadedPort;
    private final LoadJobCategoryPort loadJobCategoryPort;
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
     *    3. portfolioTags / externalLinks 가공 (도메인은 가공된 값을 그대로 받음)
     *    4. 포트폴리오 데이터 삽입
     *
     *  [부수효과]
     *    UserStorageUsage 갱신은 ImageUploadedEvent 핸들러(storage BC)가 트랜잭션 커밋 이후
     *    비동기로 처리합니다. 본 UseCase는 동기적으로 직접 호출하지 않습니다 (ADR #172 Phase 3).
     * */
    @Override
    @Transactional
    public Long execute(Long memberId, Command command) {
        List<Long> imageIds = aggregateImageIds(command.thumbnailImageId(), command.contentImageIds());

        markImagesAsUploadedPort.markUploaded(memberId, imageIds);

        Long jobCategoryId = loadJobCategoryPort.findIdByCode(command.jobCategory().code());
        Portfolio portfolio = buildPortfolio(memberId, command, jobCategoryId);

        return savePortfolioPort.save(portfolio).getId();
    }

    private Portfolio buildPortfolio(Long memberId, Command command, Long jobCategoryId) {
        SharedCommandDto.RichTextContent content = command.content();
        List<PortfolioTag> portfolioTags = toPortfolioTags(command.tags());
        List<ExternalLink> externalLinks = toExternalLinks(command.externalLinks());

        return Portfolio.create(
                memberId,
                command.thumbnailImageId(),
                command.linkedCoverLetterId(),
                command.linkedResumeId(),
                command.title(),
                command.previewSummary(),
                command.privateMemo(),
                jobCategoryId,
                command.jobCategory().userInput(),
                serializeContentJson(content),
                content.html(),
                command.contentImageIds(),
                externalLinks,
                portfolioTags,
                command.collaborationType(),
                command.visibility()
        );
    }

    private static List<PortfolioTag> toPortfolioTags(List<SharedCommandDto.SequentialTag> tagCommands) {
        if (tagCommands == null || tagCommands.isEmpty()) {
            return List.of();
        }
        return tagCommands.stream()
                .map(tag -> PortfolioTag.create(tag.name(), tag.sortOrder()))
                .toList();
    }

    private static List<ExternalLink> toExternalLinks(List<SharedCommandDto.ExternalLink> linkCommands) {
        if (linkCommands == null || linkCommands.isEmpty()) {
            return List.of();
        }
        return linkCommands.stream()
                .map(link -> new ExternalLink(link.label(), link.url()))
                .toList();
    }

    private String serializeContentJson(SharedCommandDto.RichTextContent content) {
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

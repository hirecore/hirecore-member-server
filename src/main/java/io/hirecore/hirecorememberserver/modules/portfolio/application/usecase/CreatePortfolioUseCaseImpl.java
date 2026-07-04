package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.CreatePortfolioUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.SavePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.vo.ReferencedImageIds;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedCommandDto;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.MarkImagesAsUploadedPort;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreatePortfolioUseCaseImpl implements CreatePortfolioUseCase {

    private final MarkImagesAsUploadedPort markImagesAsUploadedPort;
    private final LoadJobCategoryPort loadJobCategoryPort;
    private final SavePortfolioPort savePortfolioPort;
    private final ObjectMapper objectMapper;

    // 이미지 메타 UPLOADED 전이 후 포트폴리오 생성 (스토리지 사용량은 커밋 후 이벤트로 비동기 갱신)
    @Override
    @Transactional
    public Long execute(Long memberId, Command command) {
        List<Long> imageIds = ReferencedImageIds.of(command.thumbnailImageId(), command.contentImageIds()).values();

        markImagesAsUploadedPort.markUploaded(memberId, imageIds);

        Long jobCategoryId = loadJobCategoryPort.findIdByCode(command.leafJobCategory().code());
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
                command.leafJobCategory().userInput(),
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
}

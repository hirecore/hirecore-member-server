package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.UpdatePortfolioUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.UpdatePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.vo.ReferencedImageIds;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedCommandDto;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.MarkImagesAsUploadedPort;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdatePortfolioUseCaseImpl implements UpdatePortfolioUseCase {

    private final LoadPortfolioPort loadPortfolioPort;
    private final UpdatePortfolioPort updatePortfolioPort;
    private final LoadJobCategoryPort loadJobCategoryPort;
    private final MarkImagesAsUploadedPort markImagesAsUploadedPort;
    private final ObjectMapper objectMapper;

    // 본인 소유 포트폴리오 수정
    @Override
    @Transactional
    public Long execute(Long portfolioId, Long viewerId, Command command) {
        Portfolio portfolio = loadPortfolioPort.findById(portfolioId)
                .orElseThrow(() -> new PortfolioApplicationException(
                        PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NOT_FOUND
                ));

        if (!portfolio.isOwnedBy(viewerId)) {
            throw new PortfolioApplicationException(PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_FORBIDDEN);
        }

        List<Long> imageIds = ReferencedImageIds.of(command.thumbnailImageId(), command.contentImageIds()).values();
        markImagesAsUploadedPort.markUploaded(viewerId, imageIds);

        Long leafJobCategoryId = loadJobCategoryPort.findIdByCode(command.leafJobCategory().code());

        SharedCommandDto.RichTextContent content = command.content();
        portfolio.modify(
                viewerId,
                command.thumbnailImageId(),
                command.linkedCoverLetterId(),
                command.linkedResumeId(),
                command.title(),
                command.previewSummary(),
                command.privateMemo(),
                leafJobCategoryId,
                command.leafJobCategory().userInput(),
                serializeContentJson(content),
                content.html(),
                command.contentImageIds(),
                toExternalLinks(command.externalLinks()),
                toPortfolioTags(command.tags()),
                command.collaborationType(),
                command.visibility()
        );

        updatePortfolioPort.update(portfolio);
        return portfolio.getId();
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

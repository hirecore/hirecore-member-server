package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.UpdatePortfolioUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.PortfolioContentCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.PortfolioExternalLinkCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.PortfolioTagCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.UpdatePortfolioCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.UpdatePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.UpdateUploadStatusOfImageFileMetaPort;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdatePortfolioUseCaseImpl implements UpdatePortfolioUseCase {

    private final LoadPortfolioPort loadPortfolioPort;
    private final UpdatePortfolioPort updatePortfolioPort;
    private final LoadJobCategoryPort loadJobCategoryPort;
    private final UpdateUploadStatusOfImageFileMetaPort updateUploadStatusOfImageFileMetaPort;
    private final ObjectMapper objectMapper;

    /**
     *  [로직 플로우]
     *   1. Portfolio aggregate 로드 → 없으면 PORTFOLIO_NOT_FOUND
     *   2. 작성자 본인 여부 검증 → 아니면 PORTFOLIO_FORBIDDEN
     *   3. 신규 이미지의 markUploaded (등록 시점과 동일 패턴, 멱등 호출)
     *   4. 카테고리 코드 해석
     *   5. portfolio.modify(...) 호출 → 도메인 invariant 재검증
     *   6. UpdatePortfolioPort.update(portfolio) 호출 (merge 경로)
     *
     *  [범위 외]
     *   - 이전 썸네일/본문 이미지의 cleanup (별도 이슈)
     */
    @Override
    @Transactional
    public Long execute(Long portfolioId, Long viewerId, UpdatePortfolioCommand command) {
        Portfolio portfolio = loadPortfolio(portfolioId);
        ensureOwner(portfolio, viewerId);

        List<Long> imageIds = aggregateImageIds(command.thumbnailImageId(), command.contentImageIds());
        updateUploadStatusOfImageFileMetaPort.markUploaded(viewerId, imageIds);

        Long jobCategoryId = loadJobCategoryPort.findIdByCode(command.jobCategory().code());

        PortfolioContentCommand content = command.content();
        portfolio.modify(
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
                toExternalLinks(command.externalLinks()),
                toPortfolioTags(command.tags()),
                command.collaborationType(),
                command.visibility()
        );

        updatePortfolioPort.update(portfolio);
        return portfolio.getId();
    }

    private Portfolio loadPortfolio(Long portfolioId) {
        return loadPortfolioPort.findPortfolio(portfolioId)
                .orElseThrow(() -> new PortfolioApplicationException(
                        PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NOT_FOUND
                ));
    }

    private void ensureOwner(Portfolio portfolio, Long viewerId) {
        if (viewerId == null || !portfolio.getMemberAccountId().equals(viewerId)) {
            throw new PortfolioApplicationException(
                    PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_FORBIDDEN
            );
        }
    }

    private static List<PortfolioTag> toPortfolioTags(List<PortfolioTagCommand> tagCommands) {
        if (tagCommands == null || tagCommands.isEmpty()) {
            return List.of();
        }
        return tagCommands.stream()
                .map(tag -> PortfolioTag.create(tag.userInputTag(), tag.sortOrder()))
                .toList();
    }

    private static List<ExternalLink> toExternalLinks(List<PortfolioExternalLinkCommand> linkCommands) {
        if (linkCommands == null || linkCommands.isEmpty()) {
            return List.of();
        }
        return linkCommands.stream()
                .map(link -> new ExternalLink(link.label(), link.url()))
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

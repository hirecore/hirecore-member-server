package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedCommandDto;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;

import java.util.List;

public interface UpdatePortfolioUseCase {

    Long execute(Long portfolioId, Long viewerId, Command command);

    record Command(
            SharedCommandDto.JobCategory jobCategory,
            CollaborationType collaborationType,
            Visibility visibility,
            String title,
            String privateMemo,
            String previewSummary,
            Long thumbnailImageId,
            List<Long> contentImageIds,
            List<SharedCommandDto.SequentialTag> tags,
            List<SharedCommandDto.ExternalLink> externalLinks,
            SharedCommandDto.RichTextContent content,
            Long linkedResumeId,
            Long linkedCoverLetterId
    ) {
        public Command {
            AssertionUtils.notNull(
                    jobCategory,
                    PortfolioApplicationExceptionCodeCluster.DetailResponse.CATEGORY_CODE_MISSING,
                    PortfolioApplicationException::new
            );
            AssertionUtils.notNull(
                    collaborationType,
                    PortfolioApplicationExceptionCodeCluster.DetailResponse.COLLABORATION_TYPE_MISSING,
                    PortfolioApplicationException::new
            );
            AssertionUtils.notNull(
                    visibility,
                    PortfolioApplicationExceptionCodeCluster.DetailResponse.VISIBILITY_MISSING,
                    PortfolioApplicationException::new
            );
            AssertionUtils.notBlank(
                    title,
                    PortfolioApplicationExceptionCodeCluster.DetailResponse.TITLE_MISSING,
                    PortfolioApplicationException::new
            );
            AssertionUtils.notNull(
                    content,
                    PortfolioApplicationExceptionCodeCluster.DetailResponse.CONTENT_MISSING,
                    PortfolioApplicationException::new
            );
        }
    }
}

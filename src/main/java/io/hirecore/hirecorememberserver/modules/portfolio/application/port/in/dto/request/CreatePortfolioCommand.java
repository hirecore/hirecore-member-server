package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;

import java.util.List;

public record CreatePortfolioCommand(
        String categoryCode,
        String customCategory,
        CollaborationType collaborationType,
        Visibility visibility,
        String title,
        String privateMemo,
        Long thumbnailImageId,
        List<Long> contentImageIds,
        List<PortfolioTagCommand> tags,
        List<ExternalLink> externalLinks,
        PortfolioContentCommand content,
        Long linkedResumeId,
        Long linkedCoverLetterId
) {
    public CreatePortfolioCommand {
        AssertionUtils.notBlank(
                categoryCode,
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

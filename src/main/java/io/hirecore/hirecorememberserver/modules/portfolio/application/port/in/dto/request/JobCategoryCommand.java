package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;

public record JobCategoryCommand(
        String code,
        String userInput
) {
    public JobCategoryCommand {
        AssertionUtils.notBlank(
                code,
                PortfolioApplicationExceptionCodeCluster.DetailResponse.CATEGORY_CODE_MISSING,
                PortfolioApplicationException::new
        );
    }
}

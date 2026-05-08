package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;

import java.util.Map;

public record PortfolioContentCommand(
        Map<String, Object> json,
        String html
) {
    public PortfolioContentCommand {
        AssertionUtils.notNull(
                json,
                PortfolioApplicationExceptionCodeCluster.DetailResponse.CONTENT_JSON_MISSING,
                PortfolioApplicationException::new
        );

        AssertionUtils.notBlank(
                html,
                PortfolioApplicationExceptionCodeCluster.DetailResponse.CONTENT_HTML_MISSING,
                PortfolioApplicationException::new
        );
    }
}

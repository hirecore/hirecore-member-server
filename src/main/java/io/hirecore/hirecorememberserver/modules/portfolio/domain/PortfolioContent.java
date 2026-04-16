package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import io.hirecore.hirecorememberserver.common.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainExceptionCodeCluster;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PortfolioContent {
    private final Long id;
    private final Long portfolioId;
    private final String contentJson;
    private final String contentHtml;

    @Builder(access = AccessLevel.PUBLIC)
    private PortfolioContent(
            Long id,
            Long portfolioId,
            String contentJson,
            String contentHtml
    ) {
        ensureInvariants(id, portfolioId, contentJson, contentHtml);

        this.id = id;
        this.portfolioId = portfolioId;
        this.contentJson = contentJson;
        this.contentHtml = contentHtml;
    }

    private static void ensureInvariants(
            Long id,
            Long portfolioId,
            String contentJson,
            String contentHtml
    ) {
        AssertionUtils.notNull(
                id,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_CONTENT_ID_MISSING,
                PortfolioDomainException::new
        );
        AssertionUtils.notNull(
                portfolioId,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_CONTENT_PORTFOLIO_ID_MISSING,
                PortfolioDomainException::new
        );
        AssertionUtils.notBlank(
                contentJson,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_CONTENT_JSON_MISSING,
                PortfolioDomainException::new
        );
        AssertionUtils.notBlank(
                contentHtml,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_CONTENT_HTML_MISSING,
                PortfolioDomainException::new
        );
    }
}

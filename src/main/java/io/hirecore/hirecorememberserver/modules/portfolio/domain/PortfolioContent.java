package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import io.hirecore.hirecorememberserver.common.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainExceptionCodeCluster;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PortfolioContent {
    /**
     * Portfolio Aggregate의 식별자와 동일한 값을 사용합니다 (JPA @MapsId 매핑).
     */
    private final Long id;
    private final String contentJson;
    private final String contentHtml;

    /**
     * [복원용 빌더]
     * 데이터베이스 등 외부 인프라에서 조회된 데이터를 도메인 객체로 복원할 때만 사용해야 합니다.
     * Application 계층에서의 임의 호출은 ArchUnit 테스트에 의해 차단됩니다.
     */
    @Builder(access = AccessLevel.PUBLIC)
    private PortfolioContent(
            Long id,
            String contentJson,
            String contentHtml
    ) {
        ensureInvariants(id, contentJson, contentHtml);

        this.id = id;
        this.contentJson = contentJson;
        this.contentHtml = contentHtml;
    }

    private static void ensureInvariants(
            Long id,
            String contentJson,
            String contentHtml
    ) {
        AssertionUtils.notNull(
                id,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_CONTENT_ID_MISSING,
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

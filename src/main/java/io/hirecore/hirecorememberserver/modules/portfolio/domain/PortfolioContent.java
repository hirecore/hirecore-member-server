package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioContentDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioContentDomainExceptionCodeCluster;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;



@Getter
public class PortfolioContent {
    /**
     * Portfolio Aggregate의 식별자와 동일한 값을 사용합니다 (JPA @MapsId 매핑).
     */
    private final Long portfolioId;
    private final String contentJson;
    private final String contentHtml;

    /**
     * [복원용 빌더]
     * 데이터베이스 등 외부 인프라에서 조회된 데이터를 도메인 객체로 복원할 때만 사용해야 합니다.
     * Application 계층에서의 임의 호출은 ArchUnit 테스트에 의해 차단됩니다.
     */
    @Builder(access = AccessLevel.PUBLIC)
    private PortfolioContent(
            Long portfolioId,
            String contentJson,
            String contentHtml
    ) {
        ensureInvariants(portfolioId, contentJson, contentHtml);

        this.portfolioId = portfolioId;
        this.contentJson = contentJson;
        this.contentHtml = contentHtml;
    }

    public static PortfolioContent create(Long portfolioId, String contentJson, String contentHtml) {
        return PortfolioContent.builder()
                .portfolioId(portfolioId)
                .contentJson(contentJson)
                .contentHtml(contentHtml)
                .build();
    }

    private static void ensureInvariants(
            Long portfolioId,
            String contentJson,
            String contentHtml
    ) {
        AssertionUtils.notNull(
                portfolioId,
                PortfolioContentDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_ID_MISSING,
                PortfolioContentDomainException::new
        );
        AssertionUtils.notBlank(
                contentJson,
                PortfolioContentDomainExceptionCodeCluster.HiddenDetailResponse.CONTENT_JSON_MISSING,
                PortfolioContentDomainException::new
        );
        AssertionUtils.notBlank(
                contentHtml,
                PortfolioContentDomainExceptionCodeCluster.HiddenDetailResponse.CONTENT_HTML_MISSING,
                PortfolioContentDomainException::new
        );
    }
}

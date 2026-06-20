package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioJobCategoryDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioJobCategoryDomainExceptionCodeCluster;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

@Getter
public class PortfolioJobCategory  {

    public static final int USER_INPUT_MAX_LENGTH = 10;

    /**
     * Portfolio Aggregate의 식별자와 동일한 값을 사용합니다 (JPA @MapsId 매핑).
     */
    private final Long portfolioId;
    private final Long jobCategoryId;
    private final String userInput;
    private final Instant connectedAt;

    @Builder(access = lombok.AccessLevel.PUBLIC)
    private PortfolioJobCategory(
            Long portfolioId,
            Long jobCategoryId,
            String userInput,
            Instant connectedAt
    ) {
        ensureInvariants(portfolioId, jobCategoryId, userInput, connectedAt);

        this.portfolioId = portfolioId;
        this.jobCategoryId = jobCategoryId;
        this.userInput = userInput;
        this.connectedAt = connectedAt;
    }

    public static PortfolioJobCategory create(Long portfolioId, Long jobCategoryId, String userInput) {
        return PortfolioJobCategory.builder()
                .portfolioId(portfolioId)
                .jobCategoryId(jobCategoryId)
                .userInput(userInput)
                .connectedAt(Instant.now())
                .build();
    }

    /**
     * 직무 카테고리 연결 정보를 새 값으로 갱신한 인스턴스를 반환합니다.
     *
     * <p>식별자({@code portfolioId})는 보존되므로 영속 계층에서 INSERT/DELETE 없이 UPDATE 로 반영됩니다.
     * {@code connectedAt} 은 {@code jobCategoryId} 또는 {@code userInput} 중 하나라도 바뀐 경우에만
     * 현재 시각으로 갱신되고, 둘 다 동일하면 기존 연결 시각을 그대로 보존합니다.</p>
     */
    public PortfolioJobCategory modify(Long jobCategoryId, String userInput) {
        boolean changed = !Objects.equals(this.jobCategoryId, jobCategoryId)
                || !Objects.equals(this.userInput, userInput);
        Instant resolvedConnectedAt = changed ? Instant.now() : this.connectedAt;
        return PortfolioJobCategory.builder()
                .portfolioId(this.portfolioId)
                .jobCategoryId(jobCategoryId)
                .userInput(userInput)
                .connectedAt(resolvedConnectedAt)
                .build();
    }

    private static void ensureInvariants(
            Long portfolioId,
            Long jobCategoryId,
            String userInput,
            Instant connectedAt
    ) {
        AssertionUtils.notNull(
                portfolioId,
                PortfolioJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_ID_MISSING,
                PortfolioJobCategoryDomainException::new
        );
        AssertionUtils.notNull(
                jobCategoryId,
                PortfolioJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.JOB_CATEGORY_ID_MISSING,
                PortfolioJobCategoryDomainException::new
        );
        AssertionUtils.isTrue(
                userInput == null || userInput.length() <= USER_INPUT_MAX_LENGTH,
                PortfolioJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.USER_INPUT_TOO_LONG,
                PortfolioJobCategoryDomainException::new
        );
        AssertionUtils.notNull(
                connectedAt,
                PortfolioJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.CONNECTED_AT_MISSING,
                PortfolioJobCategoryDomainException::new
        );
    }
}

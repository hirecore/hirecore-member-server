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

    private final Long portfolioId;
    private final Long leafJobCategoryId;
    private final String userInput;
    private final Instant connectedAt;

    @Builder(access = lombok.AccessLevel.PUBLIC)
    private PortfolioJobCategory(
            Long portfolioId,
            Long leafJobCategoryId,
            String userInput,
            Instant connectedAt
    ) {
        ensureInvariants(portfolioId, leafJobCategoryId, userInput, connectedAt);

        this.portfolioId = portfolioId;
        this.leafJobCategoryId = leafJobCategoryId;
        this.userInput = userInput;
        this.connectedAt = connectedAt;
    }

    public static PortfolioJobCategory create(Long portfolioId, Long leafJobCategoryId, String userInput) {
        return PortfolioJobCategory.builder()
                .portfolioId(portfolioId)
                .leafJobCategoryId(leafJobCategoryId)
                .userInput(userInput)
                .connectedAt(Instant.now())
                .build();
    }

    public PortfolioJobCategory modify(Long leafJobCategoryId, String userInput) {
        boolean changedConnectedAt =
                !Objects.equals(this.leafJobCategoryId, leafJobCategoryId)
                || !Objects.equals(this.userInput, userInput);

        Instant connectedAt = changedConnectedAt ? Instant.now() : this.connectedAt;

        return PortfolioJobCategory.builder()
                .portfolioId(this.portfolioId)
                .leafJobCategoryId(leafJobCategoryId)
                .userInput(userInput)
                .connectedAt(connectedAt)
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

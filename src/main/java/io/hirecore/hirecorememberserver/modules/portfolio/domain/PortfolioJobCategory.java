package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioJobCategoryDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioJobCategoryDomainExceptionCodeCluster;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class PortfolioJobCategory  {

    public static final int USER_INPUT_MAX_LENGTH = 10;

    private final Long id;
    private final Long jobCategoryId;
    private final String userInput;
    private final Instant connectedAt;

    @Builder(access = lombok.AccessLevel.PUBLIC)
    private PortfolioJobCategory(
            Long id,
            Long jobCategoryId,
            String userInput,
            Instant connectedAt
    ) {
        ensureInvariants(id, jobCategoryId, userInput, connectedAt);

        this.id = id;
        this.jobCategoryId = jobCategoryId;
        this.userInput = userInput;
        this.connectedAt = connectedAt;
    }

    public static PortfolioJobCategory create(Long jobCategoryId, String userInput) {
        return PortfolioJobCategory.builder()
                .id(TsidCreator.getTsid().toLong())
                .jobCategoryId(jobCategoryId)
                .userInput(userInput)
                .connectedAt(Instant.now())
                .build();
    }

    private static void ensureInvariants(
            Long id,
            Long jobCategoryId,
            String userInput,
            Instant connectedAt
    ) {
        AssertionUtils.notNull(
                id,
                PortfolioJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
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

package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioJobCategoryDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioJobCategoryDomainExceptionCodeCluster;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class PortfolioJobCategory  {
    private final Long id;
    private final Long jobCategoryId;
    private final String customJobCategoryName;
    private final Instant isDeleted;
    private final Instant deletedAt;
    private final Instant connectedAt;

    @Builder(access = lombok.AccessLevel.PUBLIC)
    private PortfolioJobCategory(
            Long id,
            Long jobCategoryId,
            String customJobCategoryName,
            Instant isDeleted,
            Instant deletedAt,
            Instant connectedAt
    ) {
        ensureInvariants(id, jobCategoryId, isDeleted, connectedAt);

        this.id = id;
        this.jobCategoryId = jobCategoryId;
        this.customJobCategoryName = customJobCategoryName;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.connectedAt = connectedAt;
    }

    private static void ensureInvariants(
            Long id,
            Long jobCategoryId,
            Instant isDeleted,
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
        AssertionUtils.notNull(
                isDeleted,
                PortfolioJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.IS_DELETED_MISSING,
                PortfolioJobCategoryDomainException::new
        );
        AssertionUtils.notNull(
                connectedAt,
                PortfolioJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.CONNECTED_AT_MISSING,
                PortfolioJobCategoryDomainException::new
        );
    }
}

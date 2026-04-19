package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import io.hirecore.hirecorememberserver.common.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioInterestDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioInterestDomainExceptionCodeCluster;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class PortfolioInterest {
    private final Long id;
    private final Long memberAccountId;
    private final Instant interestAt;
    private final Boolean isDeleted;
    private final Instant deletedAt;

    @Builder(access = AccessLevel.PUBLIC)
    private PortfolioInterest(
            Long id,
            Long memberAccountId,
            Instant interestAt,
            Boolean isDeleted,
            Instant deletedAt
    ) {
        ensureInvariants(id, memberAccountId, interestAt);

        this.id = id;
        this.memberAccountId = memberAccountId;
        this.interestAt = interestAt;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
    }

    private static void ensureInvariants(
            Long id,
            Long memberAccountId,
            Instant interestAt
    ) {
        AssertionUtils.notNull(
                id,
                PortfolioInterestDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                PortfolioInterestDomainException::new
        );
        AssertionUtils.notNull(
                memberAccountId,
                PortfolioInterestDomainExceptionCodeCluster.HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING,
                PortfolioInterestDomainException::new
        );
        AssertionUtils.notNull(
                interestAt,
                PortfolioInterestDomainExceptionCodeCluster.HiddenDetailResponse.INTEREST_AT_MISSING,
                PortfolioInterestDomainException::new
        );
    }
}

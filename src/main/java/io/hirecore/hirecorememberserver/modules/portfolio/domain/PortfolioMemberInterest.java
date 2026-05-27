package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioMemberInterestDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioMemberInterestDomainExceptionCodeCluster;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class PortfolioMemberInterest {
    private final Long id;
    private final Long memberAccountId;
    private final Instant interestAt;
    private final Boolean isDeleted;
    private final Instant deletedAt;

    @Builder(access = AccessLevel.PUBLIC)
    private PortfolioMemberInterest(
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
                PortfolioMemberInterestDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                PortfolioMemberInterestDomainException::new
        );
        AssertionUtils.notNull(
                memberAccountId,
                PortfolioMemberInterestDomainExceptionCodeCluster.HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING,
                PortfolioMemberInterestDomainException::new
        );
        AssertionUtils.notNull(
                interestAt,
                PortfolioMemberInterestDomainExceptionCodeCluster.HiddenDetailResponse.INTEREST_AT_MISSING,
                PortfolioMemberInterestDomainException::new
        );
    }
}

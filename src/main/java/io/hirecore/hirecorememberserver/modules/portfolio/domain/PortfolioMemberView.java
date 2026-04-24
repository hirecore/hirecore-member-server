package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioMemberViewDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioMemberViewDomainExceptionCodeCluster;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class PortfolioMemberView {
    private final Long id;
    private final Long memberAccountId;
    private final Instant viewedAt;

    @Builder(access = AccessLevel.PUBLIC)
    private PortfolioMemberView(
            Long id,
            Long memberAccountId,
            Instant viewedAt
    ) {
        ensureInvariants(id, memberAccountId, viewedAt);

        this.id = id;
        this.memberAccountId = memberAccountId;
        this.viewedAt = viewedAt;
    }

    private static void ensureInvariants(
            Long id,
            Long memberAccountId,
            Instant viewedAt
    ) {
        AssertionUtils.notNull(
                id,
                PortfolioMemberViewDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                PortfolioMemberViewDomainException::new
        );
        AssertionUtils.notNull(
                memberAccountId,
                PortfolioMemberViewDomainExceptionCodeCluster.HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING,
                PortfolioMemberViewDomainException::new
        );
        AssertionUtils.notNull(
                viewedAt,
                PortfolioMemberViewDomainExceptionCodeCluster.HiddenDetailResponse.VIEWED_AT_MISSING,
                PortfolioMemberViewDomainException::new
        );
    }
}

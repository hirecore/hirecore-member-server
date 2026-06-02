package io.hirecore.hirecorememberserver.modules.portfolio.domain.event;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioMemberViewDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioMemberViewDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;

public record PortfolioViewedEvent(
        Long portfolioId,
        Long viewerMemberAccountId
) {
    public PortfolioViewedEvent {
        AssertionUtils.notNull(
                portfolioId,
                PortfolioMemberViewDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                PortfolioMemberViewDomainException::new
        );
        AssertionUtils.notNull(
                viewerMemberAccountId,
                PortfolioMemberViewDomainExceptionCodeCluster.HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING,
                PortfolioMemberViewDomainException::new
        );
    }
}

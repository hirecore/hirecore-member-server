package io.hirecore.hirecorememberserver.modules.portfolio.domain.event;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioMemberInterestDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioMemberInterestDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;

public record PortfolioInterestRegisteredEvent(
        Long portfolioId,
        Long memberAccountId
) {
    public PortfolioInterestRegisteredEvent {
        AssertionUtils.notNull(
                portfolioId,
                PortfolioMemberInterestDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                PortfolioMemberInterestDomainException::new
        );
        AssertionUtils.notNull(
                memberAccountId,
                PortfolioMemberInterestDomainExceptionCodeCluster.HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING,
                PortfolioMemberInterestDomainException::new
        );
    }
}

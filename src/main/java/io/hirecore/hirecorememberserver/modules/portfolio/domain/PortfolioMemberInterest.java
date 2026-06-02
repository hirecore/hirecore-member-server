package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import com.github.f4b6a3.tsid.TsidCreator;
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

    @Builder(access = AccessLevel.PUBLIC)
    private PortfolioMemberInterest(
            Long id,
            Long memberAccountId,
            Instant interestAt
    ) {
        ensureInvariants(id, memberAccountId, interestAt);

        this.id = id;
        this.memberAccountId = memberAccountId;
        this.interestAt = interestAt;
    }

    public static PortfolioMemberInterest create(Long memberAccountId) {
        return PortfolioMemberInterest.builder()
                .id(TsidCreator.getTsid().toLong())
                .memberAccountId(memberAccountId)
                .interestAt(Instant.now())
                .build();
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

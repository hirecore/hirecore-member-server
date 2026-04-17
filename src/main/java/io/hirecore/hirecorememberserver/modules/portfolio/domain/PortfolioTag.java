package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import io.hirecore.hirecorememberserver.common.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioTagDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioTagDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class PortfolioTag {
    private final Long id;
    private final Long portfolioId;
    private final String userInputTag;
    private final String normalizedTag;
    private final Boolean isDeleted;
    private final Instant deletedAt;
    private final AuditingInfo auditingInfo;

    @Builder(access = AccessLevel.PRIVATE)
    private PortfolioTag(
            Long id,
            Long portfolioId,
            String userInputTag,
            String normalizedTag,
            Boolean isDeleted,
            Instant deletedAt,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(id, portfolioId, userInputTag, normalizedTag, auditingInfo);

        this.id = id;
        this.portfolioId = portfolioId;
        this.userInputTag = userInputTag;
        this.normalizedTag = normalizedTag;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.auditingInfo = auditingInfo;
    }

    private static void ensureInvariants(
            Long id,
            Long portfolioId,
            String userInputTag,
            String normalizedTag,
            AuditingInfo auditingInfo
    ) {
        AssertionUtils.notNull(
                id,
                PortfolioTagDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                PortfolioTagDomainException::new
        );
        AssertionUtils.notNull(
                portfolioId,
                PortfolioTagDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_ID_MISSING,
                PortfolioTagDomainException::new
        );
        AssertionUtils.notBlank(
                userInputTag,
                PortfolioTagDomainExceptionCodeCluster.HiddenDetailResponse.USER_INPUT_TAG_MISSING,
                PortfolioTagDomainException::new
        );
        AssertionUtils.notBlank(
                normalizedTag,
                PortfolioTagDomainExceptionCodeCluster.HiddenDetailResponse.NORMALIZED_TAG_MISSING,
                PortfolioTagDomainException::new
        );
        AssertionUtils.notNull(
                auditingInfo,
                PortfolioTagDomainExceptionCodeCluster.HiddenDetailResponse.AUDITING_INFO_MISSING,
                PortfolioTagDomainException::new
        );
    }
}

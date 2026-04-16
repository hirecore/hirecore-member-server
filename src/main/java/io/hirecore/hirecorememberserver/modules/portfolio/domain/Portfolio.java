package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import io.hirecore.hirecorememberserver.common.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class Portfolio extends AbstractDomainEventPublisher implements DomainAggregateRoot {
    private final Long id;
    private final Long memberAccountId;
    private final Long portfolioCategoryId;
    /** (선택) 포트폴리오 썸네일 null가능 */
    private final Long thumbnailImageId;
    /** (선택) 연결된 자기소개서, null가능 */
    private final Long coverLetterId;
    /** (선택) 연결된 이력서, null가능 */
    private final Long resumeId;
    private final String title;
    private final String previewSummary;
    private final PortfolioContent portfolioContent;
    private final String status;
    private final Boolean visibility;
    private final Instant updatedAt;
    private final AuditingInfo auditingInfo;

    @Builder(access = AccessLevel.PUBLIC)
    private Portfolio(
            Long id,
            Long memberAccountId,
            Long portfolioCategoryId,
            Long thumbnailImageId,
            Long coverLetterId,
            Long resumeId,
            String title,
            String previewSummary,
            PortfolioContent portfolioContent,
            String status,
            Boolean visibility,
            Instant updatedAt,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(
                id, memberAccountId, portfolioCategoryId, title,
                previewSummary, portfolioContent, status, visibility,
                updatedAt, auditingInfo
        );

        this.id = id;
        this.memberAccountId = memberAccountId;
        this.portfolioCategoryId = portfolioCategoryId;
        this.thumbnailImageId = thumbnailImageId;
        this.coverLetterId = coverLetterId;
        this.resumeId = resumeId;
        this.title = title;
        this.previewSummary = previewSummary;
        this.portfolioContent = portfolioContent;
        this.status = status;
        this.visibility = visibility;
        this.updatedAt = updatedAt;
        this.auditingInfo = auditingInfo;
    }

    private static void ensureInvariants(
            Long id,
            Long memberAccountId,
            Long portfolioCategoryId,
            String title,
            String previewSummary,
            PortfolioContent portfolioContent,
            String status,
            Boolean visibility,
            Instant updatedAt,
            AuditingInfo auditingInfo
    ) {
        AssertionUtils.notNull(
                id,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                PortfolioDomainException::new
        );
        AssertionUtils.notNull(
                memberAccountId,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING,
                PortfolioDomainException::new
        );
        AssertionUtils.notNull(
                portfolioCategoryId,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_CATEGORY_ID_MISSING,
                PortfolioDomainException::new
        );
        AssertionUtils.notBlank(
                title,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.TITLE_MISSING,
                PortfolioDomainException::new
        );
        AssertionUtils.notBlank(
                previewSummary,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PREVIEW_SUMMARY_MISSING,
                PortfolioDomainException::new
        );
        AssertionUtils.notNull(
                portfolioContent,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_CONTENT_MISSING,
                PortfolioDomainException::new
        );
        AssertionUtils.notNull(
                status,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.STATUS_MISSING,
                PortfolioDomainException::new
        );
        AssertionUtils.notNull(
                visibility,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.VISIBILITY_MISSING,
                PortfolioDomainException::new
        );
        AssertionUtils.notNull(
                updatedAt,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.UPDATED_AT_MISSING,
                PortfolioDomainException::new
        );
        AssertionUtils.notNull(
                auditingInfo,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.AUDITING_INFO_MISSING,
                PortfolioDomainException::new
        );
    }
}

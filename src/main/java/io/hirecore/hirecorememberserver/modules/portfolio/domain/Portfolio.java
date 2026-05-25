package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.vo.PortfolioStatus;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import io.hirecore.hirecorememberserver.sharedkernel.domain.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.domain.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class Portfolio extends AbstractDomainEventPublisher implements DomainAggregateRoot {

    public static final int PREVIEW_SUMMARY_MAX_LENGTH = 100;

    private final Long id;
    private final Long memberAccountId;
    private final PortfolioJobCategory portfolioJobCategory;
    /** (선택) 포트폴리오 썸네일 null가능 */
    private final Long thumbnailImageId;
    /** (선택) 연결된 자기소개서, null가능 */
    private final Long coverLetterId;
    /** (선택) 연결된 이력서, null가능 */
    private final Long resumeId;
    private final String title;
    private final String previewSummary;
    private final PortfolioContent portfolioContent;
    private final List<ExternalLink> externalLinks;
    /** (선택) 나만보기 메모, null가능 */
    private final String privateMemo;
    /** (선택) 포트폴리오 태그 목록, null가능 */
    private final List<PortfolioTag> portfolioTags;
    private final PortfolioStatus status;
    private final CollaborationType collaborationType;
    private final Visibility visibility;
    private final AuditingInfo auditingInfo;

    /**
     * [복원용 빌더]
     * 데이터베이스 등 외부 인프라에서 조회된 데이터를 도메인 객체로 복원할 때만 사용해야 합니다.
     * Application 계층에서의 임의 호출은 ArchUnit 테스트에 의해 차단됩니다.
     */
    @Builder(access = AccessLevel.PUBLIC)
    private Portfolio(
            Long id,
            Long memberAccountId,
            PortfolioJobCategory portfolioJobCategory,
            Long thumbnailImageId,
            Long coverLetterId,
            Long resumeId,
            String title,
            String previewSummary,
            PortfolioContent portfolioContent,
            List<ExternalLink> externalLinks,
            String privateMemo,
            List<PortfolioTag> portfolioTags,
            PortfolioStatus status,
            CollaborationType collaborationType,
            Visibility visibility,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(
                id, memberAccountId, portfolioJobCategory, title,
                previewSummary, portfolioContent, status, collaborationType, visibility, auditingInfo
        );

        this.id = id;
        this.memberAccountId = memberAccountId;
        this.portfolioJobCategory = portfolioJobCategory;
        this.thumbnailImageId = thumbnailImageId;
        this.coverLetterId = coverLetterId;
        this.resumeId = resumeId;
        this.title = title;
        this.previewSummary = previewSummary;
        this.portfolioContent = portfolioContent;
        this.externalLinks = externalLinks;
        this.privateMemo = privateMemo;
        this.portfolioTags = portfolioTags;
        this.status = status;
        this.collaborationType = collaborationType;
        this.visibility = visibility;
        this.auditingInfo = auditingInfo;
    }

    public static Portfolio create(
            Long memberAccountId,
            Long jobCategoryId,
            String userInput,
            Long thumbnailImageId,
            Long coverLetterId,
            Long resumeId,
            String title,
            String previewSummary,
            String contentJson,
            String contentHtml,
            List<ExternalLink> externalLinks,
            String privateMemo,
            List<PortfolioTag> portfolioTags,
            CollaborationType collaborationType,
            Visibility visibility
    ) {
        Long portfolioId = TsidCreator.getTsid().toLong();

        return Portfolio.builder()
                .id(portfolioId)
                .memberAccountId(memberAccountId)
                .portfolioJobCategory(PortfolioJobCategory.create(jobCategoryId, userInput))
                .thumbnailImageId(thumbnailImageId)
                .coverLetterId(coverLetterId)
                .resumeId(resumeId)
                .title(title)
                .previewSummary(previewSummary)
                .portfolioContent(PortfolioContent.create(portfolioId, contentJson, contentHtml))
                .externalLinks(externalLinks)
                .privateMemo(privateMemo)
                .portfolioTags(portfolioTags)
                .status(PortfolioStatus.PUBLISHED)
                .collaborationType(collaborationType)
                .visibility(visibility)
                .auditingInfo(AuditingInfo.create())
                .build();
    }

    private static void ensureInvariants(
            Long id,
            Long memberAccountId,
            PortfolioJobCategory portfolioJobCategory,
            String title,
            String previewSummary,
            PortfolioContent portfolioContent,
            PortfolioStatus status,
            CollaborationType collaborationType,
            Visibility visibility,
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
                portfolioJobCategory,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_JOB_CATEGORY_MISSING,
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
        AssertionUtils.isTrue(
                previewSummary.length() <= PREVIEW_SUMMARY_MAX_LENGTH,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PREVIEW_SUMMARY_TOO_LONG,
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
                collaborationType,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.COLLABORATION_TYPE_MISSING,
                PortfolioDomainException::new
        );
        AssertionUtils.notNull(
                visibility,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.VISIBILITY_MISSING,
                PortfolioDomainException::new
        );
        AssertionUtils.notNull(
                auditingInfo,
                SharedKernelExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                PortfolioDomainException::new
        );
    }
}

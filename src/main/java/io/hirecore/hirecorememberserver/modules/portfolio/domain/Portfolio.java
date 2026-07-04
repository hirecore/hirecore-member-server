package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.PortfolioImagesUnlinkedEvent;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.event.PortfolioInterestCancelledEvent;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.event.PortfolioInterestRegisteredEvent;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.event.PortfolioViewedEvent;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.vo.PortfolioStatus;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.vo.ReferencedImageIds;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import io.hirecore.hirecorememberserver.sharedkernel.domain.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.domain.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
public class Portfolio extends AbstractDomainEventPublisher implements DomainAggregateRoot {

    public static final int TITLE_MAX_LENGTH = 80;
    public static final int PREVIEW_SUMMARY_MAX_LENGTH = 100;
    public static final int PRIVATE_MEMO_MAX_LENGTH = 200;
    public static final int EXTERNAL_LINKS_MAX_COUNT = 6;
    public static final int PORTFOLIO_TAGS_MAX_COUNT = 10;

    private final Long id;
    private final Long memberAccountId;
    private Long thumbnailImageId;
    private Long coverLetterId;
    private Long resumeId;
    private String title;
    private String previewSummary;
    private String privateMemo;
    private final Long cachedViewCount;
    private final Long cachedInterestCount;

    // sub-aggregate
    private PortfolioJobCategory portfolioJobCategory;
    private PortfolioContent portfolioContent;
    private List<PortfolioTag> portfolioTags;

    // vo
    private List<ExternalLink> externalLinks;
    private final PortfolioStatus status;
    private CollaborationType collaborationType;
    private Visibility visibility;
    private AuditingInfo auditingInfo;

    // 복원용 빌더 (인프라 조회 전용, ArchUnit으로 임의 호출 차단)
    @Builder(access = AccessLevel.PUBLIC)
    private Portfolio(
            Long id,
            Long memberAccountId,
            Long thumbnailImageId,
            Long coverLetterId,
            Long resumeId,
            String title,
            String previewSummary,
            String privateMemo,
            Long cachedViewCount,
            Long cachedInterestCount,
            PortfolioJobCategory portfolioJobCategory,
            PortfolioContent portfolioContent,
            List<ExternalLink> externalLinks,
            List<PortfolioTag> portfolioTags,
            PortfolioStatus status,
            CollaborationType collaborationType,
            Visibility visibility,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(
                id, memberAccountId, title, previewSummary, privateMemo,
                portfolioJobCategory, portfolioContent, externalLinks, portfolioTags,
                status, collaborationType, visibility, auditingInfo
        );

        this.id = id;
        this.memberAccountId = memberAccountId;
        this.thumbnailImageId = thumbnailImageId;
        this.coverLetterId = coverLetterId;
        this.resumeId = resumeId;
        this.title = title;
        this.previewSummary = previewSummary;
        this.privateMemo = privateMemo;
        this.cachedViewCount = cachedViewCount;
        this.cachedInterestCount = cachedInterestCount;
        this.portfolioJobCategory = portfolioJobCategory;
        this.portfolioContent = portfolioContent;
        this.externalLinks = externalLinks == null ? List.of() : List.copyOf(externalLinks);
        this.portfolioTags = portfolioTags == null ? List.of() : List.copyOf(portfolioTags);
        this.status = status;
        this.collaborationType = collaborationType;
        this.visibility = visibility;
        this.auditingInfo = auditingInfo;
    }

    public static Portfolio create(
            Long memberAccountId,
            Long thumbnailImageId,
            Long coverLetterId,
            Long resumeId,
            String title,
            String previewSummary,
            String privateMemo,
            Long jobCategoryId,
            String userInput,
            String contentJson,
            String contentHtml,
            List<Long> contentImageIds,
            List<ExternalLink> externalLinks,
            List<PortfolioTag> portfolioTags,
            CollaborationType collaborationType,
            Visibility visibility
    ) {
        Long portfolioId = TsidCreator.getTsid().toLong();

        return Portfolio.builder()
                .id(portfolioId)
                .memberAccountId(memberAccountId)
                .thumbnailImageId(thumbnailImageId)
                .coverLetterId(coverLetterId)
                .resumeId(resumeId)
                .title(title)
                .previewSummary(previewSummary)
                .privateMemo(privateMemo)
                .cachedViewCount(0L)
                .cachedInterestCount(0L)
                .portfolioJobCategory(PortfolioJobCategory.create(portfolioId, jobCategoryId, userInput))
                .portfolioContent(PortfolioContent.create(
                        portfolioId,
                        contentJson,
                        contentHtml,
                        contentImageIds != null ? contentImageIds : List.of()
                ))
                .externalLinks(externalLinks)
                .portfolioTags(portfolioTags)
                .status(PortfolioStatus.PUBLISHED)
                .collaborationType(collaborationType)
                .visibility(visibility)
                .auditingInfo(AuditingInfo.create())
                .build();
    }

    public void modify(
            Long requestMemberAccountId,
            Long thumbnailImageId,
            Long coverLetterId,
            Long resumeId,
            String title,
            String previewSummary,
            String privateMemo,
            Long leafJobCategoryId,
            String jobCategoryUserInput,
            String contentJson,
            String contentHtml,
            List<Long> contentImageIds,
            List<ExternalLink> externalLinks,
            List<PortfolioTag> portfolioTags,
            CollaborationType collaborationType,
            Visibility visibility
    ) {
        ensurePortfolioOwner(requestMemberAccountId);

        PortfolioJobCategory jobCategory = this.portfolioJobCategory.modify(leafJobCategoryId, jobCategoryUserInput);
        contentImageIds = contentImageIds != null ? contentImageIds : List.of();

        PortfolioContent newContent = PortfolioContent.create(
                this.id,
                contentJson,
                contentHtml,
                contentImageIds
        );

        ensureInvariants(
                this.id, this.memberAccountId, title, previewSummary, privateMemo,
                jobCategory, newContent, externalLinks, portfolioTags,
                this.status, collaborationType, visibility, this.auditingInfo
        );

        List<Long> releasedImageIds = ReferencedImageIds.of(this.thumbnailImageId, this.portfolioContent.getImageIds())
                .minus(ReferencedImageIds.of(thumbnailImageId, contentImageIds));

        this.thumbnailImageId = thumbnailImageId;
        this.coverLetterId = coverLetterId;
        this.resumeId = resumeId;
        this.title = title;
        this.previewSummary = previewSummary;
        this.privateMemo = privateMemo;
        this.portfolioJobCategory = jobCategory;
        this.portfolioContent = newContent;
        this.externalLinks = externalLinks == null ? List.of() : List.copyOf(externalLinks);
        this.portfolioTags = portfolioTags == null ? List.of() : List.copyOf(portfolioTags);
        this.collaborationType = collaborationType;
        this.visibility = visibility;
        this.auditingInfo = this.auditingInfo.update();

        if (!releasedImageIds.isEmpty()) {
            registerEvent(new PortfolioImagesUnlinkedEvent(
                    this.id,
                    this.memberAccountId,
                    releasedImageIds
            ));
        }
    }

    // 조회수 정책(소유자 제외/첫 조회만) 검증 후 이벤트 발행, alreadyViewed로 멱등 처리
    public boolean markViewedBy(Long viewerMemberAccountId, boolean alreadyViewed) {
        if (this.memberAccountId.equals(viewerMemberAccountId)) {
            return false;
        }
        if (alreadyViewed) {
            return false;
        }
        registerEvent(new PortfolioViewedEvent(this.id, viewerMemberAccountId));
        return true;
    }

    // 관심 등록 정책(소유자/비공개 금지) 검증 후 이벤트 등록, alreadyInterested로 멱등 처리
    public void registerInterestBy(Long memberAccountId, boolean alreadyInterested) {
        if (this.memberAccountId.equals(memberAccountId)) {
            throw new PortfolioDomainException(PortfolioDomainExceptionCodeCluster.DetailResponse.INTEREST_OWNER_NOT_ALLOWED);
        }
        if (!this.visibility.isPublic()) {
            throw new PortfolioDomainException(PortfolioDomainExceptionCodeCluster.DetailResponse.INTEREST_PORTFOLIO_FORBIDDEN);
        }
        if (alreadyInterested) {
            return;
        }
        registerEvent(new PortfolioInterestRegisteredEvent(this.id, memberAccountId));
    }

    // 관심 해제 이벤트 등록 (소비자 단에서 멱등 처리)
    public void cancelInterestBy(Long memberAccountId) {
        registerEvent(new PortfolioInterestCancelledEvent(this.id, memberAccountId));
    }

    public boolean isOwnedBy(Long memberAccountId) {
        return this.memberAccountId.equals(memberAccountId);
    }

    // 공개는 누구나, 비공개는 소유자만 조회 가능
    public boolean isViewableBy(Long memberAccountId) {
        return this.visibility.isPublic() || isOwnedBy(memberAccountId);
    }

    // 본체·본문·태그 수정 시각 중 최신값
    public Instant latestUpdatedAt() {
        Stream<Instant> base = Stream.of(
                this.auditingInfo.updatedAt(),
                this.portfolioContent.getAuditingInfo().updatedAt()
        );
        Stream<Instant> tagUpdates = this.portfolioTags == null
                ? Stream.empty()
                : this.portfolioTags.stream().map(tag -> tag.getAuditingInfo().updatedAt());
        return Stream.concat(base, tagUpdates)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    // 소유자 검증 후 참조 이미지 unlink 이벤트 발행 (참조 없으면 비공집합 invariant 위반이라 생략)
    public void delete(Long requesterMemberAccountId) {
        if (!this.memberAccountId.equals(requesterMemberAccountId)) {
            throw new PortfolioDomainException(PortfolioDomainExceptionCodeCluster.DetailResponse.PORTFOLIO_DELETE_DENIED);
        }
        List<Long> unlinkedImageIds = referencedImageIds();
        if (!unlinkedImageIds.isEmpty()) {
            registerEvent(new PortfolioImagesUnlinkedEvent(
                    this.id,
                    this.memberAccountId,
                    unlinkedImageIds
            ));
        }
    }

    // 썸네일+본문 참조 이미지 집합 (순서 보존, 중복 제거)
    public List<Long> referencedImageIds() {
        List<Long> contentImageIds = this.portfolioContent != null ? this.portfolioContent.getImageIds() : null;
        return ReferencedImageIds.of(this.thumbnailImageId, contentImageIds).values();
    }

    private static void ensureInvariants(
            Long id,
            Long memberAccountId,
            String title,
            String previewSummary,
            String privateMemo,
            PortfolioJobCategory portfolioJobCategory,
            PortfolioContent portfolioContent,
            List<ExternalLink> externalLinks,
            List<PortfolioTag> portfolioTags,
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
        AssertionUtils.notBlank(
                title,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.TITLE_MISSING,
                PortfolioDomainException::new
        );
        AssertionUtils.isTrue(
                title.length() <= TITLE_MAX_LENGTH,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.TITLE_TOO_LONG,
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
        AssertionUtils.isTrue(
                privateMemo == null || privateMemo.length() <= PRIVATE_MEMO_MAX_LENGTH,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PRIVATE_MEMO_TOO_LONG,
                PortfolioDomainException::new
        );
        AssertionUtils.notNull(
                portfolioJobCategory,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_JOB_CATEGORY_MISSING,
                PortfolioDomainException::new
        );
        AssertionUtils.notNull(
                portfolioContent,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_CONTENT_MISSING,
                PortfolioDomainException::new
        );
        AssertionUtils.isTrue(
                externalLinks == null || externalLinks.size() <= EXTERNAL_LINKS_MAX_COUNT,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.EXTERNAL_LINKS_TOO_MANY,
                PortfolioDomainException::new
        );
        AssertionUtils.isTrue(
                portfolioTags == null || portfolioTags.size() <= PORTFOLIO_TAGS_MAX_COUNT,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_TAGS_TOO_MANY,
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

    private void ensurePortfolioOwner(Long requestMemberAccountId) {
        AssertionUtils.isEqual(
                requestMemberAccountId,
                this.memberAccountId,
                PortfolioDomainExceptionCodeCluster.DetailResponse.PORTFOLIO_DELETE_DENIED,
                PortfolioDomainException::new
        );
    }
}

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
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import io.hirecore.hirecorememberserver.sharedkernel.domain.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.domain.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    /**
     * [복원용 빌더]
     * 데이터베이스 등 외부 인프라에서 조회된 데이터를 도메인 객체로 복원할 때만 사용해야 합니다.
     * Application 계층에서의 임의 호출은 ArchUnit 테스트에 의해 차단됩니다.
     */
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
        this.externalLinks = externalLinks;
        this.portfolioTags = portfolioTags;
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

        List<Long> releasedImageIds = computeReleasedImageIds(
                this.thumbnailImageId, this.portfolioContent.getImageIds(),
                thumbnailImageId, contentImageIds
        );

        this.thumbnailImageId = thumbnailImageId;
        this.coverLetterId = coverLetterId;
        this.resumeId = resumeId;
        this.title = title;
        this.previewSummary = previewSummary;
        this.privateMemo = privateMemo;
        this.portfolioJobCategory = jobCategory;
        this.portfolioContent = newContent;
        this.externalLinks = externalLinks != null ? externalLinks : List.of();
        this.portfolioTags = portfolioTags != null ? portfolioTags : List.of();
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

    /**
     * 조회수 기록 정책(소유자 제외 / 첫 조회만 카운트) 을 검증하고 통과 시 {@link PortfolioViewedEvent}를 발행합니다.
     *
     * <p>첫 조회 여부는 set-based 제약이므로 호출 측에서 미리 확인한 결과를 {@code alreadyViewed} 로 받아 멱등 처리합니다.
     * 통과 여부는 boolean 으로 반환하며, 실제 {@code PortfolioMemberView} 적재와 {@code cachedViewCount} 증가는
     * 이벤트 소비자가 처리합니다.</p>
     */
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

    /**
     * 관심 등록 정책(소유자 금지 / 비공개 금지)을 검증하고 {@link PortfolioInterestRegisteredEvent}를 등록합니다.
     *
     * <p>중복 검사는 set-based 제약이므로 호출 측에서 미리 확인한 결과를 {@code alreadyInterested}로 받아 멱등 처리합니다.
     * 실제 {@code PortfolioMemberInterest} 적재와 {@code cachedInterestCount} 증가는 이벤트 소비자가 처리합니다.</p>
     */
    public void registerInterestBy(Long memberAccountId, boolean alreadyInterested) {
        if (this.memberAccountId.equals(memberAccountId)) {
            throw new PortfolioDomainException(PortfolioDomainExceptionCodeCluster.DetailResponse.INTEREST_OWNER_NOT_ALLOWED);
        }
        if (this.visibility != Visibility.PUBLIC) {
            throw new PortfolioDomainException(PortfolioDomainExceptionCodeCluster.DetailResponse.INTEREST_PORTFOLIO_FORBIDDEN);
        }
        if (alreadyInterested) {
            return;
        }
        registerEvent(new PortfolioInterestRegisteredEvent(this.id, memberAccountId));
    }

    /**
     * 관심 해제 의사를 표기하고 {@link PortfolioInterestCancelledEvent}를 등록합니다.
     *
     * <p>실제 {@code PortfolioMemberInterest} 삭제와 {@code cachedInterestCount} 감소(실제 삭제가 발생한 경우에 한정)는
     * 이벤트 소비자가 처리합니다. 등록된 관심이 없는 사용자의 호출은 소비자 단에서 멱등으로 종료됩니다.</p>
     */
    public void cancelInterestBy(Long memberAccountId) {
        registerEvent(new PortfolioInterestCancelledEvent(this.id, memberAccountId));
    }

    /**
     * 작성자 본인의 영구 삭제 의사를 검증하고, 본문/썸네일에서 참조 중이던 이미지들을 ORPHANED 대상으로
     * {@link PortfolioImagesUnlinkedEvent} 에 실어 발행합니다.
     *
     * <p>소유자 검증 위반 시 {@link PortfolioDomainExceptionCodeCluster.DetailResponse#PORTFOLIO_DELETE_DENIED}
     * 도메인 예외를 던집니다. 참조 이미지가 한 건도 없는 경우 {@code PortfolioImagesUnlinkedEvent} 의
     * invariant(비공집합) 에 부합하지 않으므로 이벤트 발행을 생략합니다.</p>
     *
     * <p>실제 영구 행 삭제와 {@code portfolio_member_interests} / {@code portfolio_member_views} 의 cleanup 은
     * 영속 어댑터가 처리합니다.</p>
     */
    public void delete(Long requesterMemberAccountId) {
        if (!this.memberAccountId.equals(requesterMemberAccountId)) {
            throw new PortfolioDomainException(PortfolioDomainExceptionCodeCluster.DetailResponse.PORTFOLIO_DELETE_DENIED);
        }
        List<Long> unlinkedImageIds = collectReferencedImageIds();
        if (!unlinkedImageIds.isEmpty()) {
            registerEvent(new PortfolioImagesUnlinkedEvent(
                    this.id,
                    this.memberAccountId,
                    unlinkedImageIds
            ));
        }
    }

    /**
     * 썸네일과 본문에서 참조 중인 이미지 식별자 집합을 입력 순서를 보존하며 중복 없이 모읍니다.
     */
    private List<Long> collectReferencedImageIds() {
        List<Long> collected = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        if (this.thumbnailImageId != null && seen.add(this.thumbnailImageId)) {
            collected.add(this.thumbnailImageId);
        }
        List<Long> contentImageIds = this.portfolioContent != null ? this.portfolioContent.getImageIds() : null;
        if (contentImageIds != null) {
            for (Long imageId : contentImageIds) {
                if (imageId != null && seen.add(imageId)) {
                    collected.add(imageId);
                }
            }
        }
        return collected;
    }

    /**
     * 이전 이미지 집합({@code (oldThumbnail ∪ oldContent)}) 에서 새 집합({@code (newThumbnail ∪ newContent)}) 을
     * 뺀 차집합 — 즉 본문/썸네일에서 더 이상 참조되지 않게 된 imageId 들을 반환합니다.
     * 입력 순서를 보존하기 위해 LinkedHashSet 의미론으로 계산합니다.
     */
    private static List<Long> computeReleasedImageIds(
            Long oldThumbnailImageId,
            List<Long> oldContentImageIds,
            Long newThumbnailImageId,
            List<Long> newContentImageIds
    ) {
        Set<Long> newSet = new HashSet<>();
        if (newThumbnailImageId != null) {
            newSet.add(newThumbnailImageId);
        }
        if (newContentImageIds != null) {
            newSet.addAll(newContentImageIds);
        }

        List<Long> released = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        if (oldThumbnailImageId != null && !newSet.contains(oldThumbnailImageId) && seen.add(oldThumbnailImageId)) {
            released.add(oldThumbnailImageId);
        }
        if (oldContentImageIds != null) {
            for (Long oldId : oldContentImageIds) {
                if (oldId != null && !newSet.contains(oldId) && seen.add(oldId)) {
                    released.add(oldId);
                }
            }
        }
        return released;
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

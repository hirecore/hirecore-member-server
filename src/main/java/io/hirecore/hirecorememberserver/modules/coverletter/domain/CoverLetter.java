package io.hirecore.hirecorememberserver.modules.coverletter.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import io.hirecore.hirecorememberserver.modules.coverletter.domain.exception.CoverLetterDomainException;
import io.hirecore.hirecorememberserver.modules.coverletter.domain.exception.CoverLetterDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.coverletter.domain.vo.CoverLetterStatus;
import io.hirecore.hirecorememberserver.sharedkernel.domain.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.domain.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CoverLetter extends AbstractDomainEventPublisher implements DomainAggregateRoot {

    public static final int TITLE_MAX_LENGTH = 80;
    public static final int PREVIEW_SUMMARY_MAX_LENGTH = 100;
    public static final int PRIVATE_MEMO_MAX_LENGTH = 200;
    public static final int EXTERNAL_LINKS_MAX_COUNT = 6;
    public static final int COVER_LETTER_TAGS_MAX_COUNT = 10;

    private final Long id;
    private final Long memberAccountId;
    private Long thumbnailImageId;
    private List<Long> portfolioIds;
    private String title;
    private String previewSummary;
    private String privateMemo;
    private final Long cachedViewCount;
    private final Long cachedInterestCount;

    // sub-aggregate
    private CoverLetterJobCategory coverLetterJobCategory;
    private CoverLetterContent coverLetterContent;
    private List<CoverLetterTag> coverLetterTags;

    // vo
    private List<ExternalLink> externalLinks;
    private final CoverLetterStatus status;
    private CollaborationType collaborationType;
    private Visibility visibility;
    private AuditingInfo auditingInfo;

    /**
     * [복원용 빌더]
     * 데이터베이스 등 외부 인프라에서 조회된 데이터를 도메인 객체로 복원할 때만 사용해야 합니다.
     * Application 계층에서의 임의 호출은 ArchUnit 테스트에 의해 차단됩니다.
     */
    @Builder(access = AccessLevel.PUBLIC)
    private CoverLetter(
            Long id,
            Long memberAccountId,
            Long thumbnailImageId,
            String title,
            String previewSummary,
            String privateMemo,
            Long cachedViewCount,
            Long cachedInterestCount,
            CoverLetterJobCategory coverLetterJobCategory,
            CoverLetterContent coverLetterContent,
            List<ExternalLink> externalLinks,
            List<CoverLetterTag> coverLetterTags,
            List<Long> portfolioIds,
            CoverLetterStatus status,
            CollaborationType collaborationType,
            Visibility visibility,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(
                id, memberAccountId, title, previewSummary, privateMemo,
                coverLetterJobCategory, coverLetterContent, externalLinks, coverLetterTags,
                status, collaborationType, visibility, auditingInfo
        );

        this.id = id;
        this.memberAccountId = memberAccountId;
        this.thumbnailImageId = thumbnailImageId;
        this.title = title;
        this.previewSummary = previewSummary;
        this.privateMemo = privateMemo;
        this.cachedViewCount = cachedViewCount;
        this.cachedInterestCount = cachedInterestCount;
        this.coverLetterJobCategory = coverLetterJobCategory;
        this.coverLetterContent = coverLetterContent;
        this.externalLinks = externalLinks;
        this.coverLetterTags = coverLetterTags;
        this.portfolioIds = portfolioIds;
        this.status = status;
        this.collaborationType = collaborationType;
        this.visibility = visibility;
        this.auditingInfo = auditingInfo;
    }

    public static CoverLetter create(
            Long memberAccountId,
            Long thumbnailImageId,
            String title,
            String previewSummary,
            String privateMemo,
            Long jobCategoryId,
            String userInput,
            String contentJson,
            String contentHtml,
            List<Long> contentImageIds,
            List<ExternalLink> externalLinks,
            List<CoverLetterTag> coverLetterTags,
            CollaborationType collaborationType,
            Visibility visibility
    ) {
        Long coverLetterId = TsidCreator.getTsid().toLong();

        return CoverLetter.builder()
                .id(coverLetterId)
                .memberAccountId(memberAccountId)
                .thumbnailImageId(thumbnailImageId)
                .title(title)
                .previewSummary(previewSummary)
                .privateMemo(privateMemo)
                .cachedViewCount(0L)
                .cachedInterestCount(0L)
                .coverLetterJobCategory(CoverLetterJobCategory.create(jobCategoryId, userInput))
                .coverLetterContent(CoverLetterContent.create(
                        coverLetterId,
                        contentJson,
                        contentHtml,
                        contentImageIds != null ? contentImageIds : List.of()
                ))
                .externalLinks(externalLinks)
                .coverLetterTags(coverLetterTags)
                .portfolioIds(List.of())
                .status(CoverLetterStatus.PUBLISHED)
                .collaborationType(collaborationType)
                .visibility(visibility)
                .auditingInfo(AuditingInfo.create())
                .build();
    }

    private static void ensureInvariants(
            Long id,
            Long memberAccountId,
            String title,
            String previewSummary,
            String privateMemo,
            CoverLetterJobCategory coverLetterJobCategory,
            CoverLetterContent coverLetterContent,
            List<ExternalLink> externalLinks,
            List<CoverLetterTag> coverLetterTags,
            CoverLetterStatus status,
            CollaborationType collaborationType,
            Visibility visibility,
            AuditingInfo auditingInfo
    ) {
        AssertionUtils.notNull(
                id,
                CoverLetterDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                CoverLetterDomainException::new
        );
        AssertionUtils.notNull(
                memberAccountId,
                CoverLetterDomainExceptionCodeCluster.HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING,
                CoverLetterDomainException::new
        );
        AssertionUtils.notBlank(
                title,
                CoverLetterDomainExceptionCodeCluster.HiddenDetailResponse.TITLE_MISSING,
                CoverLetterDomainException::new
        );
        AssertionUtils.isTrue(
                title.length() <= TITLE_MAX_LENGTH,
                CoverLetterDomainExceptionCodeCluster.HiddenDetailResponse.TITLE_TOO_LONG,
                CoverLetterDomainException::new
        );
        AssertionUtils.notBlank(
                previewSummary,
                CoverLetterDomainExceptionCodeCluster.HiddenDetailResponse.PREVIEW_SUMMARY_MISSING,
                CoverLetterDomainException::new
        );
        AssertionUtils.isTrue(
                previewSummary.length() <= PREVIEW_SUMMARY_MAX_LENGTH,
                CoverLetterDomainExceptionCodeCluster.HiddenDetailResponse.PREVIEW_SUMMARY_TOO_LONG,
                CoverLetterDomainException::new
        );
        AssertionUtils.isTrue(
                privateMemo == null || privateMemo.length() <= PRIVATE_MEMO_MAX_LENGTH,
                CoverLetterDomainExceptionCodeCluster.HiddenDetailResponse.PRIVATE_MEMO_TOO_LONG,
                CoverLetterDomainException::new
        );
        AssertionUtils.notNull(
                coverLetterJobCategory,
                CoverLetterDomainExceptionCodeCluster.HiddenDetailResponse.COVER_LETTER_JOB_CATEGORY_MISSING,
                CoverLetterDomainException::new
        );
        AssertionUtils.notNull(
                coverLetterContent,
                CoverLetterDomainExceptionCodeCluster.HiddenDetailResponse.COVER_LETTER_CONTENT_MISSING,
                CoverLetterDomainException::new
        );
        AssertionUtils.isTrue(
                externalLinks == null || externalLinks.size() <= EXTERNAL_LINKS_MAX_COUNT,
                CoverLetterDomainExceptionCodeCluster.HiddenDetailResponse.EXTERNAL_LINKS_TOO_MANY,
                CoverLetterDomainException::new
        );
        AssertionUtils.isTrue(
                coverLetterTags == null || coverLetterTags.size() <= COVER_LETTER_TAGS_MAX_COUNT,
                CoverLetterDomainExceptionCodeCluster.HiddenDetailResponse.COVER_LETTER_TAGS_TOO_MANY,
                CoverLetterDomainException::new
        );
        AssertionUtils.notNull(
                status,
                CoverLetterDomainExceptionCodeCluster.HiddenDetailResponse.STATUS_MISSING,
                CoverLetterDomainException::new
        );
        AssertionUtils.notNull(
                collaborationType,
                CoverLetterDomainExceptionCodeCluster.HiddenDetailResponse.COLLABORATION_TYPE_MISSING,
                CoverLetterDomainException::new
        );
        AssertionUtils.notNull(
                visibility,
                CoverLetterDomainExceptionCodeCluster.HiddenDetailResponse.VISIBILITY_MISSING,
                CoverLetterDomainException::new
        );
        AssertionUtils.notNull(
                auditingInfo,
                SharedKernelExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                CoverLetterDomainException::new
        );
    }
}

package io.hirecore.hirecorememberserver.modules.resume.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import io.hirecore.hirecorememberserver.modules.resume.domain.exception.ResumeDomainException;
import io.hirecore.hirecorememberserver.modules.resume.domain.exception.ResumeDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.resume.domain.vo.ResumeStatus;
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
public class Resume extends AbstractDomainEventPublisher implements DomainAggregateRoot {

    public static final int TITLE_MAX_LENGTH = 80;
    public static final int PREVIEW_SUMMARY_MAX_LENGTH = 100;
    public static final int PRIVATE_MEMO_MAX_LENGTH = 200;
    public static final int EXTERNAL_LINKS_MAX_COUNT = 6;
    public static final int RESUME_TAGS_MAX_COUNT = 10;

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
    private ResumeJobCategory resumeJobCategory;
    private ResumeContent resumeContent;
    private List<ResumeTag> resumeTags;

    // vo
    private List<ExternalLink> externalLinks;
    private final ResumeStatus status;
    private CollaborationType collaborationType;
    private Visibility visibility;
    private AuditingInfo auditingInfo;

    /**
     * [복원용 빌더]
     * 데이터베이스 등 외부 인프라에서 조회된 데이터를 도메인 객체로 복원할 때만 사용해야 합니다.
     * Application 계층에서의 임의 호출은 ArchUnit 테스트에 의해 차단됩니다.
     */
    @Builder(access = AccessLevel.PUBLIC)
    private Resume(
            Long id,
            Long memberAccountId,
            Long thumbnailImageId,
            List<Long> portfolioIds,
            String title,
            String previewSummary,
            String privateMemo,
            Long cachedViewCount,
            Long cachedInterestCount,
            ResumeJobCategory resumeJobCategory,
            ResumeContent resumeContent,
            List<ExternalLink> externalLinks,
            List<ResumeTag> resumeTags,
            ResumeStatus status,
            CollaborationType collaborationType,
            Visibility visibility,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(
                id, memberAccountId, title, previewSummary, privateMemo,
                resumeJobCategory, resumeContent, externalLinks, resumeTags,
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
        this.resumeJobCategory = resumeJobCategory;
        this.resumeContent = resumeContent;
        this.externalLinks = externalLinks;
        this.resumeTags = resumeTags;
        this.portfolioIds = portfolioIds;
        this.status = status;
        this.collaborationType = collaborationType;
        this.visibility = visibility;
        this.auditingInfo = auditingInfo;
    }

    public static Resume create(
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
            List<ResumeTag> resumeTags,
            CollaborationType collaborationType,
            Visibility visibility
    ) {
        Long resumeId = TsidCreator.getTsid().toLong();

        return Resume.builder()
                .id(resumeId)
                .memberAccountId(memberAccountId)
                .thumbnailImageId(thumbnailImageId)
                .title(title)
                .previewSummary(previewSummary)
                .privateMemo(privateMemo)
                .cachedViewCount(0L)
                .cachedInterestCount(0L)
                .resumeJobCategory(ResumeJobCategory.create(jobCategoryId, userInput))
                .resumeContent(ResumeContent.create(
                        resumeId,
                        contentJson,
                        contentHtml,
                        contentImageIds != null ? contentImageIds : List.of()
                ))
                .externalLinks(externalLinks)
                .resumeTags(resumeTags)
                .portfolioIds(List.of())
                .status(ResumeStatus.PUBLISHED)
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
            ResumeJobCategory resumeJobCategory,
            ResumeContent resumeContent,
            List<ExternalLink> externalLinks,
            List<ResumeTag> resumeTags,
            ResumeStatus status,
            CollaborationType collaborationType,
            Visibility visibility,
            AuditingInfo auditingInfo
    ) {
        AssertionUtils.notNull(
                id,
                ResumeDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                ResumeDomainException::new
        );
        AssertionUtils.notNull(
                memberAccountId,
                ResumeDomainExceptionCodeCluster.HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING,
                ResumeDomainException::new
        );
        AssertionUtils.notBlank(
                title,
                ResumeDomainExceptionCodeCluster.HiddenDetailResponse.TITLE_MISSING,
                ResumeDomainException::new
        );
        AssertionUtils.isTrue(
                title.length() <= TITLE_MAX_LENGTH,
                ResumeDomainExceptionCodeCluster.HiddenDetailResponse.TITLE_TOO_LONG,
                ResumeDomainException::new
        );
        AssertionUtils.notBlank(
                previewSummary,
                ResumeDomainExceptionCodeCluster.HiddenDetailResponse.PREVIEW_SUMMARY_MISSING,
                ResumeDomainException::new
        );
        AssertionUtils.isTrue(
                previewSummary.length() <= PREVIEW_SUMMARY_MAX_LENGTH,
                ResumeDomainExceptionCodeCluster.HiddenDetailResponse.PREVIEW_SUMMARY_TOO_LONG,
                ResumeDomainException::new
        );
        AssertionUtils.isTrue(
                privateMemo == null || privateMemo.length() <= PRIVATE_MEMO_MAX_LENGTH,
                ResumeDomainExceptionCodeCluster.HiddenDetailResponse.PRIVATE_MEMO_TOO_LONG,
                ResumeDomainException::new
        );
        AssertionUtils.notNull(
                resumeJobCategory,
                ResumeDomainExceptionCodeCluster.HiddenDetailResponse.RESUME_JOB_CATEGORY_MISSING,
                ResumeDomainException::new
        );
        AssertionUtils.notNull(
                resumeContent,
                ResumeDomainExceptionCodeCluster.HiddenDetailResponse.RESUME_CONTENT_MISSING,
                ResumeDomainException::new
        );
        AssertionUtils.isTrue(
                externalLinks == null || externalLinks.size() <= EXTERNAL_LINKS_MAX_COUNT,
                ResumeDomainExceptionCodeCluster.HiddenDetailResponse.EXTERNAL_LINKS_TOO_MANY,
                ResumeDomainException::new
        );
        AssertionUtils.isTrue(
                resumeTags == null || resumeTags.size() <= RESUME_TAGS_MAX_COUNT,
                ResumeDomainExceptionCodeCluster.HiddenDetailResponse.RESUME_TAGS_TOO_MANY,
                ResumeDomainException::new
        );
        AssertionUtils.notNull(
                status,
                ResumeDomainExceptionCodeCluster.HiddenDetailResponse.STATUS_MISSING,
                ResumeDomainException::new
        );
        AssertionUtils.notNull(
                collaborationType,
                ResumeDomainExceptionCodeCluster.HiddenDetailResponse.COLLABORATION_TYPE_MISSING,
                ResumeDomainException::new
        );
        AssertionUtils.notNull(
                visibility,
                ResumeDomainExceptionCodeCluster.HiddenDetailResponse.VISIBILITY_MISSING,
                ResumeDomainException::new
        );
        AssertionUtils.notNull(
                auditingInfo,
                SharedKernelExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                ResumeDomainException::new
        );
    }
}

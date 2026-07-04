package io.hirecore.hirecorememberserver.modules.coverletter.domain;

import io.hirecore.hirecorememberserver.modules.coverletter.domain.exception.CoverLetterContentDomainException;
import io.hirecore.hirecorememberserver.modules.coverletter.domain.exception.CoverLetterContentDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CoverLetterContent {
    // CoverLetter 식별자와 동일 (@MapsId)
    private final Long coverLetterId;
    private final String contentJson;
    private final String contentHtml;
    // 본문에 임베드된 이미지 ID 집합 (수정 시 차집합으로 고아 이미지 회수)
    private final List<Long> imageIds;
    private final AuditingInfo auditingInfo;

    // 복원 전용 빌더 (인프라 조회 → 도메인, Application 직접 호출은 ArchUnit 차단)
    @Builder(access = AccessLevel.PUBLIC)
    private CoverLetterContent(
            Long coverLetterId,
            String contentJson,
            String contentHtml,
            List<Long> imageIds,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(coverLetterId, contentJson, contentHtml, imageIds, auditingInfo);

        this.coverLetterId = coverLetterId;
        this.contentJson = contentJson;
        this.contentHtml = contentHtml;
        this.imageIds = List.copyOf(imageIds);
        this.auditingInfo = auditingInfo;
    }

    public static CoverLetterContent create(
            Long coverLetterId,
            String contentJson,
            String contentHtml,
            List<Long> imageIds
    ) {
        return CoverLetterContent.builder()
                .coverLetterId(coverLetterId)
                .contentJson(contentJson)
                .contentHtml(contentHtml)
                .imageIds(imageIds)
                .auditingInfo(AuditingInfo.create())
                .build();
    }

    private static void ensureInvariants(
            Long coverLetterId,
            String contentJson,
            String contentHtml,
            List<Long> imageIds,
            AuditingInfo auditingInfo
    ) {
        AssertionUtils.notNull(
                coverLetterId,
                CoverLetterContentDomainExceptionCodeCluster.HiddenDetailResponse.COVER_LETTER_ID_MISSING,
                CoverLetterContentDomainException::new
        );
        AssertionUtils.notBlank(
                contentJson,
                CoverLetterContentDomainExceptionCodeCluster.HiddenDetailResponse.CONTENT_JSON_MISSING,
                CoverLetterContentDomainException::new
        );
        AssertionUtils.notBlank(
                contentHtml,
                CoverLetterContentDomainExceptionCodeCluster.HiddenDetailResponse.CONTENT_HTML_MISSING,
                CoverLetterContentDomainException::new
        );
        AssertionUtils.notNull(
                imageIds,
                CoverLetterContentDomainExceptionCodeCluster.HiddenDetailResponse.IMAGE_IDS_MISSING,
                CoverLetterContentDomainException::new
        );
        ensureImageIdsAreClean(imageIds);
        AssertionUtils.notNull(
                auditingInfo,
                SharedKernelExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                CoverLetterContentDomainException::new
        );
    }

    private static void ensureImageIdsAreClean(List<Long> imageIds) {
        for (Long imageId : imageIds) {
            if (imageId == null) {
                throw new CoverLetterContentDomainException(
                        CoverLetterContentDomainExceptionCodeCluster.HiddenDetailResponse.IMAGE_IDS_CONTAINS_NULL
                );
            }
        }
    }
}

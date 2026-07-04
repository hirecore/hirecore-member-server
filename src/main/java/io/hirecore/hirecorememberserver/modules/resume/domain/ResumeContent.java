package io.hirecore.hirecorememberserver.modules.resume.domain;

import io.hirecore.hirecorememberserver.modules.resume.domain.exception.ResumeContentDomainException;
import io.hirecore.hirecorememberserver.modules.resume.domain.exception.ResumeContentDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ResumeContent {
    // Resume 식별자와 동일 (@MapsId)
    private final Long resumeId;
    private final String contentJson;
    private final String contentHtml;
    // 본문에 임베드된 이미지 ID 집합 (수정 시 차집합으로 고아 이미지 회수)
    private final List<Long> imageIds;
    private final AuditingInfo auditingInfo;

    // 복원 전용 빌더 (인프라 조회 → 도메인, Application 직접 호출은 ArchUnit 차단)
    @Builder(access = AccessLevel.PUBLIC)
    private ResumeContent(
            Long resumeId,
            String contentJson,
            String contentHtml,
            List<Long> imageIds,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(resumeId, contentJson, contentHtml, imageIds, auditingInfo);

        this.resumeId = resumeId;
        this.contentJson = contentJson;
        this.contentHtml = contentHtml;
        this.imageIds = List.copyOf(imageIds);
        this.auditingInfo = auditingInfo;
    }

    public static ResumeContent create(
            Long resumeId,
            String contentJson,
            String contentHtml,
            List<Long> imageIds
    ) {
        return ResumeContent.builder()
                .resumeId(resumeId)
                .contentJson(contentJson)
                .contentHtml(contentHtml)
                .imageIds(imageIds)
                .auditingInfo(AuditingInfo.create())
                .build();
    }

    private static void ensureInvariants(
            Long resumeId,
            String contentJson,
            String contentHtml,
            List<Long> imageIds,
            AuditingInfo auditingInfo
    ) {
        AssertionUtils.notNull(
                resumeId,
                ResumeContentDomainExceptionCodeCluster.HiddenDetailResponse.RESUME_ID_MISSING,
                ResumeContentDomainException::new
        );
        AssertionUtils.notBlank(
                contentJson,
                ResumeContentDomainExceptionCodeCluster.HiddenDetailResponse.CONTENT_JSON_MISSING,
                ResumeContentDomainException::new
        );
        AssertionUtils.notBlank(
                contentHtml,
                ResumeContentDomainExceptionCodeCluster.HiddenDetailResponse.CONTENT_HTML_MISSING,
                ResumeContentDomainException::new
        );
        AssertionUtils.notNull(
                imageIds,
                ResumeContentDomainExceptionCodeCluster.HiddenDetailResponse.IMAGE_IDS_MISSING,
                ResumeContentDomainException::new
        );
        ensureImageIdsAreClean(imageIds);
        AssertionUtils.notNull(
                auditingInfo,
                SharedKernelExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                ResumeContentDomainException::new
        );
    }

    private static void ensureImageIdsAreClean(List<Long> imageIds) {
        for (Long imageId : imageIds) {
            if (imageId == null) {
                throw new ResumeContentDomainException(
                        ResumeContentDomainExceptionCodeCluster.HiddenDetailResponse.IMAGE_IDS_CONTAINS_NULL
                );
            }
        }
    }
}

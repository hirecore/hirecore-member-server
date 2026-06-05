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
    /**
     * Resume Aggregate의 식별자와 동일한 값을 사용합니다 (JPA @MapsId 매핑).
     */
    private final Long resumeId;
    private final String contentJson;
    private final String contentHtml;
    /**
     * 본문에서 사용 중인 이미지 식별자 집합입니다.
     * 본문 JSON/HTML 내부에 임베드된 이미지와 일치하는 ID 들만 보관하며,
     * 이력서 수정 시 차집합 계산을 통해 고아 이미지 회수 트리거에 사용됩니다.
     */
    private final List<Long> imageIds;
    private final AuditingInfo auditingInfo;

    /**
     * [복원용 빌더]
     * 데이터베이스 등 외부 인프라에서 조회된 데이터를 도메인 객체로 복원할 때만 사용해야 합니다.
     * Application 계층에서의 임의 호출은 ArchUnit 테스트에 의해 차단됩니다.
     */
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

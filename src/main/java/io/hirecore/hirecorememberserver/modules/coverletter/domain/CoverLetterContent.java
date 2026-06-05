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
    /**
     * CoverLetter Aggregate의 식별자와 동일한 값을 사용합니다 (JPA @MapsId 매핑).
     */
    private final Long coverLetterId;
    private final String contentJson;
    private final String contentHtml;
    /**
     * 본문에서 사용 중인 이미지 식별자 집합입니다.
     * 본문 JSON/HTML 내부에 임베드된 이미지와 일치하는 ID 들만 보관하며,
     * 자기소개서 수정 시 차집합 계산을 통해 고아 이미지 회수 트리거에 사용됩니다.
     */
    private final List<Long> imageIds;
    private final AuditingInfo auditingInfo;

    /**
     * [복원용 빌더]
     * 데이터베이스 등 외부 인프라에서 조회된 데이터를 도메인 객체로 복원할 때만 사용해야 합니다.
     * Application 계층에서의 임의 호출은 ArchUnit 테스트에 의해 차단됩니다.
     */
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

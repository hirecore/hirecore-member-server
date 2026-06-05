package io.hirecore.hirecorememberserver.modules.coverletter.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import io.hirecore.hirecorememberserver.modules.coverletter.domain.exception.CoverLetterTagDomainException;
import io.hirecore.hirecorememberserver.modules.coverletter.domain.exception.CoverLetterTagDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CoverLetterTag {
    private final Long id;
    private final String userInputTag;
    private final String normalizedTag;
    private final Integer sortOrder;
    private final AuditingInfo auditingInfo;

    /**
     * [복원용 빌더]
     * 데이터베이스 등 외부 인프라에서 조회된 데이터를 도메인 객체로 복원할 때만 사용해야 합니다.
     * Application 계층에서의 임의 호출은 ArchUnit 테스트에 의해 차단됩니다.
     */
    @Builder(access = AccessLevel.PUBLIC)
    private CoverLetterTag(
            Long id,
            String userInputTag,
            String normalizedTag,
            Integer sortOrder,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(id, userInputTag, normalizedTag, sortOrder, auditingInfo);

        this.id = id;
        this.userInputTag = userInputTag;
        this.normalizedTag = normalizedTag;
        this.sortOrder = sortOrder;
        this.auditingInfo = auditingInfo;
    }

    public static CoverLetterTag create(String userInputTag, Integer sortOrder) {
        return CoverLetterTag.builder()
                .id(TsidCreator.getTsid().toLong())
                .userInputTag(userInputTag)
                .normalizedTag(normalize(userInputTag))
                .sortOrder(sortOrder)
                .auditingInfo(AuditingInfo.create())
                .build();
    }

    private static String normalize(String userInputTag) {
        if (userInputTag == null) {
            return null;
        }
        return userInputTag.trim().toLowerCase().replaceAll("\\s+", "_");
    }

    private static void ensureInvariants(
            Long id,
            String userInputTag,
            String normalizedTag,
            Integer sortOrder,
            AuditingInfo auditingInfo
    ) {
        AssertionUtils.notNull(
                id,
                CoverLetterTagDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                CoverLetterTagDomainException::new
        );
        AssertionUtils.notBlank(
                userInputTag,
                CoverLetterTagDomainExceptionCodeCluster.HiddenDetailResponse.USER_INPUT_TAG_MISSING,
                CoverLetterTagDomainException::new
        );
        AssertionUtils.notBlank(
                normalizedTag,
                CoverLetterTagDomainExceptionCodeCluster.HiddenDetailResponse.NORMALIZED_TAG_MISSING,
                CoverLetterTagDomainException::new
        );
        AssertionUtils.notNull(
                sortOrder,
                CoverLetterTagDomainExceptionCodeCluster.HiddenDetailResponse.SORT_ORDER_MISSING,
                CoverLetterTagDomainException::new
        );
        AssertionUtils.isTrue(
                sortOrder >= 0,
                CoverLetterTagDomainExceptionCodeCluster.HiddenDetailResponse.SORT_ORDER_NEGATIVE,
                CoverLetterTagDomainException::new
        );
        AssertionUtils.notNull(
                auditingInfo,
                SharedKernelExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                CoverLetterTagDomainException::new
        );
    }
}

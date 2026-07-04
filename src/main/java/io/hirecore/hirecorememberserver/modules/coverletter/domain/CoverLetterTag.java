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
    private final String name;
    private final String normalizedTag;
    private final Integer sortOrder;
    private final AuditingInfo auditingInfo;

    // 복원 전용 빌더 (인프라 조회 → 도메인, Application 직접 호출은 ArchUnit 차단)
    @Builder(access = AccessLevel.PUBLIC)
    private CoverLetterTag(
            Long id,
            String name,
            String normalizedTag,
            Integer sortOrder,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(id, name, normalizedTag, sortOrder, auditingInfo);

        this.id = id;
        this.name = name;
        this.normalizedTag = normalizedTag;
        this.sortOrder = sortOrder;
        this.auditingInfo = auditingInfo;
    }

    public static CoverLetterTag create(String name, Integer sortOrder) {
        return CoverLetterTag.builder()
                .id(TsidCreator.getTsid().toLong())
                .name(name)
                .normalizedTag(normalize(name))
                .sortOrder(sortOrder)
                .auditingInfo(AuditingInfo.create())
                .build();
    }

    private static String normalize(String name) {
        if (name == null) {
            return null;
        }
        return name.trim().toLowerCase().replaceAll("\\s+", "_");
    }

    private static void ensureInvariants(
            Long id,
            String name,
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
                name,
                CoverLetterTagDomainExceptionCodeCluster.HiddenDetailResponse.NAME_MISSING,
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

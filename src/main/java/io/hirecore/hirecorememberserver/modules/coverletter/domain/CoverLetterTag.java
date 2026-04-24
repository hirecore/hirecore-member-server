package io.hirecore.hirecorememberserver.modules.coverletter.domain;

import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.coverletter.domain.exception.CoverLetterTagDomainException;
import io.hirecore.hirecorememberserver.modules.coverletter.domain.exception.CoverLetterTagDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class CoverLetterTag {
    private final Long id;
    private final String userInputTag;
    private final String normalizedTag;
    private final Boolean isDeleted;
    private final Instant deletedAt;
    private final AuditingInfo auditingInfo;

    @Builder(access = AccessLevel.PRIVATE)
    private CoverLetterTag(
            Long id,
            String userInputTag,
            String normalizedTag,
            Boolean isDeleted,
            Instant deletedAt,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(id, userInputTag, normalizedTag, auditingInfo);

        this.id = id;
        this.userInputTag = userInputTag;
        this.normalizedTag = normalizedTag;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.auditingInfo = auditingInfo;
    }

    private static void ensureInvariants(
            Long id,
            String userInputTag,
            String normalizedTag,
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
                auditingInfo,
                SharedKernelExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                CoverLetterTagDomainException::new
        );
    }
}

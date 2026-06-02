package io.hirecore.hirecorememberserver.modules.resume.domain;

import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.resume.domain.exception.ResumeTagDomainException;
import io.hirecore.hirecorememberserver.modules.resume.domain.exception.ResumeTagDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class ResumeTag {
    private final Long id;
    private final String userInputTag;
    private final String normalizedTag;
    private final AuditingInfo auditingInfo;

    @Builder(access = AccessLevel.PRIVATE)
    private ResumeTag(
            Long id,
            String userInputTag,
            String normalizedTag,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(id, userInputTag, normalizedTag, auditingInfo);

        this.id = id;
        this.userInputTag = userInputTag;
        this.normalizedTag = normalizedTag;
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
                ResumeTagDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                ResumeTagDomainException::new
        );
        AssertionUtils.notBlank(
                userInputTag,
                ResumeTagDomainExceptionCodeCluster.HiddenDetailResponse.USER_INPUT_TAG_MISSING,
                ResumeTagDomainException::new
        );
        AssertionUtils.notBlank(
                normalizedTag,
                ResumeTagDomainExceptionCodeCluster.HiddenDetailResponse.NORMALIZED_TAG_MISSING,
                ResumeTagDomainException::new
        );
        AssertionUtils.notNull(
                auditingInfo,
                SharedKernelExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                ResumeTagDomainException::new
        );
    }
}

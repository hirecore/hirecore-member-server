package io.hirecore.hirecorememberserver.modules.coverletter.domain;

import io.hirecore.hirecorememberserver.common.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.coverletter.domain.exception.CoverLetterTagDomainException;
import io.hirecore.hirecorememberserver.modules.coverletter.domain.exception.CoverLetterTagDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class CoverLetterTag {
    private final Long id;
    private final Long coverLetterId;
    private final String userInputTag;
    private final String normalizedTag;
    private final Boolean isDeleted;
    private final Instant deletedAt;
    private final AuditingInfo auditingInfo;

    @Builder(access = AccessLevel.PRIVATE)
    private CoverLetterTag(
            Long id,
            Long coverLetterId,
            String userInputTag,
            String normalizedTag,
            Boolean isDeleted,
            Instant deletedAt,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(id, coverLetterId, userInputTag, normalizedTag, auditingInfo);

        this.id = id;
        this.coverLetterId = coverLetterId;
        this.userInputTag = userInputTag;
        this.normalizedTag = normalizedTag;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.auditingInfo = auditingInfo;
    }

    private static void ensureInvariants(
            Long id,
            Long coverLetterId,
            String userInputTag,
            String normalizedTag,
            AuditingInfo auditingInfo
    ) {
        AssertionUtils.notNull(
                id,
                CoverLetterTagDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                CoverLetterTagDomainException::new
        );
        AssertionUtils.notNull(
                coverLetterId,
                CoverLetterTagDomainExceptionCodeCluster.HiddenDetailResponse.COVER_LETTER_ID_MISSING,
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
                CoverLetterTagDomainExceptionCodeCluster.HiddenDetailResponse.AUDITING_INFO_MISSING,
                CoverLetterTagDomainException::new
        );
    }
}

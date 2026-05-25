package io.hirecore.hirecorememberserver.modules.resume.domain;

import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.resume.domain.exception.ResumeJobCategoryDomainException;
import io.hirecore.hirecorememberserver.modules.resume.domain.exception.ResumeJobCategoryDomainExceptionCodeCluster;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class ResumeJobCategory {

    public static final int USER_INPUT_MAX_LENGTH = 10;

    private final Long id;
    private final Long jobCategoryId;
    private final String userInput;
    private final Instant isDeleted;
    private final Instant deletedAt;
    private final Instant connectedAt;

    @Builder(access = lombok.AccessLevel.PUBLIC)
    private ResumeJobCategory(
            Long id,
            Long jobCategoryId,
            String userInput,
            Instant isDeleted,
            Instant deletedAt,
            Instant connectedAt
    ) {
        ensureInvariants(id, jobCategoryId, userInput, isDeleted, connectedAt);

        this.id = id;
        this.jobCategoryId = jobCategoryId;
        this.userInput = userInput;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.connectedAt = connectedAt;
    }

    private static void ensureInvariants(
            Long id,
            Long jobCategoryId,
            String userInput,
            Instant isDeleted,
            Instant connectedAt
    ) {
        AssertionUtils.notNull(
                id,
                ResumeJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                ResumeJobCategoryDomainException::new
        );
        AssertionUtils.notNull(
                jobCategoryId,
                ResumeJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.JOB_CATEGORY_ID_MISSING,
                ResumeJobCategoryDomainException::new
        );
        AssertionUtils.isTrue(
                userInput == null || userInput.length() <= USER_INPUT_MAX_LENGTH,
                ResumeJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.USER_INPUT_TOO_LONG,
                ResumeJobCategoryDomainException::new
        );
        AssertionUtils.notNull(
                isDeleted,
                ResumeJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.IS_DELETED_MISSING,
                ResumeJobCategoryDomainException::new
        );
        AssertionUtils.notNull(
                connectedAt,
                ResumeJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.CONNECTED_AT_MISSING,
                ResumeJobCategoryDomainException::new
        );
    }
}

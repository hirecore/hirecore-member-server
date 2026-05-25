package io.hirecore.hirecorememberserver.modules.coverletter.domain;

import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.coverletter.domain.exception.CoverLetterJobCategoryDomainException;
import io.hirecore.hirecorememberserver.modules.coverletter.domain.exception.CoverLetterJobCategoryDomainExceptionCodeCluster;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class CoverLetterJobCategory {

    public static final int USER_INPUT_MAX_LENGTH = 10;

    private final Long id;
    private final Long jobCategoryId;
    private final String userInput;
    private final Instant isDeleted;
    private final Instant deletedAt;
    private final Instant connectedAt;

    @Builder(access = lombok.AccessLevel.PUBLIC)
    private CoverLetterJobCategory(
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
                CoverLetterJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                CoverLetterJobCategoryDomainException::new
        );
        AssertionUtils.notNull(
                jobCategoryId,
                CoverLetterJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.JOB_CATEGORY_ID_MISSING,
                CoverLetterJobCategoryDomainException::new
        );
        AssertionUtils.isTrue(
                userInput == null || userInput.length() <= USER_INPUT_MAX_LENGTH,
                CoverLetterJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.USER_INPUT_TOO_LONG,
                CoverLetterJobCategoryDomainException::new
        );
        AssertionUtils.notNull(
                isDeleted,
                CoverLetterJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.IS_DELETED_MISSING,
                CoverLetterJobCategoryDomainException::new
        );
        AssertionUtils.notNull(
                connectedAt,
                CoverLetterJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.CONNECTED_AT_MISSING,
                CoverLetterJobCategoryDomainException::new
        );
    }
}

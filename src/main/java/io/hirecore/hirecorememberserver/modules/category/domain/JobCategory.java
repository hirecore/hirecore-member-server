package io.hirecore.hirecorememberserver.modules.category.domain;

import io.hirecore.hirecorememberserver.common.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.category.domain.exception.JobCategoryDomainException;
import io.hirecore.hirecorememberserver.modules.category.domain.exception.JobCategoryDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class JobCategory extends AbstractDomainEventPublisher implements DomainAggregateRoot {
    private final Long id;
    private final Long parentId;
    private final String jobCategoryCode;
    private final String categoryName;
    private final Integer depth;
    private final Boolean isActivity;
    private final Boolean allowsCustomInput;
    private final Integer sortOrder;
    private final AuditingInfo auditingInfo;

    /**
     * [복원용 빌더]
     * 데이터베이스 등 외부 인프라에서 조회된 데이터를 도메인 객체로 복원할 때만 사용해야 합니다.
     * Application 계층에서의 임의 호출은 ArchUnit 테스트에 의해 차단됩니다.
     */
    @Builder(access = AccessLevel.PUBLIC)
    private JobCategory(
        Long id,
        Long parentId,
        String jobCategoryCode,
        String categoryName,
        Integer depth,
        Boolean isActivity,
        Boolean allowsCustomInput,
        Integer sortOrder,
        AuditingInfo auditingInfo
    ) {

        ensureInvariants(
                id, jobCategoryCode, categoryName, depth, isActivity,
                allowsCustomInput, sortOrder, auditingInfo
        );

        this.id = id;
        this.parentId = parentId;
        this.jobCategoryCode = jobCategoryCode;
        this.categoryName = categoryName;
        this.depth = depth;
        this.isActivity = isActivity;
        this.allowsCustomInput = allowsCustomInput;
        this.sortOrder = sortOrder;
        this.auditingInfo = auditingInfo;
    }

    private static void ensureInvariants(
            Long id,
            String jobCategoryCode,
            String categoryName,
            Integer depth,
            Boolean isActivity,
            Boolean allowsCustomInput,
            Integer sortOrder,
            AuditingInfo auditingInfo
    ) {
        AssertionUtils.notNull(
                id,
                JobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.JOB_CATEGORY_ID_MISSING,
                JobCategoryDomainException::new
        );
        AssertionUtils.notBlank(
                jobCategoryCode,
                JobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.JOB_CATEGORY_CODE_MISSING,
                JobCategoryDomainException::new
        );
        AssertionUtils.notBlank(
                categoryName,
                JobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.JOB_CATEGORY_NAME_MISSING,
                JobCategoryDomainException::new
        );
        AssertionUtils.notNull(
                depth,
                JobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.JOB_CATEGORY_DEPTH_MISSING,
                JobCategoryDomainException::new
        );
        AssertionUtils.notNull(
                isActivity,
                JobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.JOB_CATEGORY_IS_ACTIVITY_MISSING,
                JobCategoryDomainException::new
        );
        AssertionUtils.notNull(
                allowsCustomInput,
                JobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.JOB_CATEGORY_ALLOWS_CUSTOM_INPUT_MISSING,
                JobCategoryDomainException::new
        );
        AssertionUtils.notNull(
                sortOrder,
                JobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.JOB_CATEGORY_SORT_ORDER_MISSING,
                JobCategoryDomainException::new
        );
        AssertionUtils.notNull(
                auditingInfo,
                JobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.JOB_CATEGORY_AUDITING_INFO_MISSING,
                JobCategoryDomainException::new
        );
    }
}

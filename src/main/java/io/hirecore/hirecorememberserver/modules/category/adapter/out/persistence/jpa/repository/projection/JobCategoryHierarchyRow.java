package io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.repository.projection;

import java.time.Instant;

/**
 * 다중 leaf 직무 계층 일괄 조회 Recursive CTE 의 평면 결과 행.
 *
 * <p>{@code start_leaf_id} 컬럼으로 어느 leaf 의 경로인지 구분되며, 어댑터는 이를 키로
 * 그룹핑하여 {@code Map<Long, List<JobCategory>>} 로 변환한다.</p>
 */
public interface JobCategoryHierarchyRow {
    Long getStartLeafId();
    Long getId();
    Long getParentId();
    String getCategoryCode();
    String getCategoryName();
    Integer getDepth();
    Boolean getIsActive();
    Boolean getIsAssignable();
    Boolean getAllowsCustomInput();
    Integer getSortOrder();
    Instant getCreatedAt();
    Instant getUpdatedAt();
}

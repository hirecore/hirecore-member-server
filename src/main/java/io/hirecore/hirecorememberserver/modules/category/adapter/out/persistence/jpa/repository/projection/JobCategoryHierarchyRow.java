package io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.repository.projection;

import java.time.Instant;

// 다중 leaf 계층 조회 CTE 의 평면 결과 행 (start_leaf_id 로 leaf 구분)
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

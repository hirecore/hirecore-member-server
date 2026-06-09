package io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.entity.JobCategoryJpaEntity;
import io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.repository.projection.JobCategoryHierarchyRow;
import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class JobCategoryJpaEntityMapper {
    public abstract JobCategoryJpaEntity toJpaEntity(JobCategory domain);
    public abstract JobCategory toDomain(JobCategoryJpaEntity jpaEntity);

    /**
     * 다중 leaf 직무 계층 일괄 조회의 Recursive CTE 결과 행({@link JobCategoryHierarchyRow}) 을
     * 도메인 객체로 복원한다.
     */
    public JobCategory toDomain(JobCategoryHierarchyRow row) {
        return JobCategory.builder()
                .id(row.getId())
                .parentId(row.getParentId())
                .categoryCode(row.getCategoryCode())
                .categoryName(row.getCategoryName())
                .depth(row.getDepth())
                .isActive(row.getIsActive())
                .isAssignable(row.getIsAssignable())
                .allowsCustomInput(row.getAllowsCustomInput())
                .sortOrder(row.getSortOrder())
                .auditingInfo(new AuditingInfo(row.getCreatedAt(), row.getUpdatedAt()))
                .build();
    }
}

package io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.entity.JobCategoryJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobCategoryJpaQueryRepository extends Repository<JobCategoryJpaEntity, Long> {

    @Query("""
            SELECT jc FROM JobCategoryJpaEntity jc
            WHERE jc.depth <= :depth
              AND jc.isActive = true
            ORDER BY jc.depth, jc.parentId, jc.sortOrder
            """)
    List<JobCategoryJpaEntity> loadByDynamicDepth(@Param("depth") Integer depth);
}

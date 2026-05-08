package io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.entity.JobCategoryJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobCategoryJpaQueryRepository extends Repository<JobCategoryJpaEntity, Long> {

    @Query("""
            SELECT jc FROM JobCategoryJpaEntity jc
            WHERE jc.depth <= :depth
              AND jc.isActive = true
            ORDER BY jc.depth, jc.parentId, jc.sortOrder
            """)
    List<JobCategoryJpaEntity> loadAllWithinDepth(@Param("depth") Integer depth);

    @Query("""
            SELECT jc FROM JobCategoryJpaEntity jc
            WHERE jc.categoryCode = :categoryCode
              AND jc.isActive = true
            """)
    Optional<JobCategoryJpaEntity> findActiveByCategoryCode(@Param("categoryCode") String categoryCode);
}

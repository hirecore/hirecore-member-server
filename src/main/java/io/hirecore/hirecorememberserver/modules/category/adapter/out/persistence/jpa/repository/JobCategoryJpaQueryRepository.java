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

    Optional<JobCategoryJpaEntity> findById(Long id);

    /**
     * 주어진 leaf id 의 카테고리부터 root 까지의 계층 경로를 단일 SQL 로 조회한다.
     *
     * <p>{@code WITH RECURSIVE} 로 leaf → root 를 한 번에 펼친 뒤 {@code depth ASC} 로 정렬해
     * 반환하므로 결과 순서는 root → leaf 다. 깊이 D 와 무관하게 발행 SQL 은 1회로 고정된다.</p>
     */
    @Query(value = """
            WITH RECURSIVE category_path (
                id, parent_id, category_code, category_name, depth,
                is_active, is_assignable, allows_custom_input, sort_order,
                created_at, updated_at
            ) AS (
                SELECT id, parent_id, category_code, category_name, depth,
                       is_active, is_assignable, allows_custom_input, sort_order,
                       created_at, updated_at
                  FROM job_category
                 WHERE id = :leafId
                UNION ALL
                SELECT jc.id, jc.parent_id, jc.category_code, jc.category_name, jc.depth,
                       jc.is_active, jc.is_assignable, jc.allows_custom_input, jc.sort_order,
                       jc.created_at, jc.updated_at
                  FROM job_category jc
                  JOIN category_path cp ON cp.parent_id = jc.id
            )
            SELECT * FROM category_path ORDER BY depth ASC
            """, nativeQuery = true)
    List<JobCategoryJpaEntity> findHierarchyPathByLeafId(@Param("leafId") Long leafId);
}

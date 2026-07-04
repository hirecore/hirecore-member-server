package io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.entity.JobCategoryJpaEntity;
import io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.repository.projection.JobCategoryHierarchyRow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JobCategoryJpaQueryRepository extends Repository<JobCategoryJpaEntity, Long> {

    @Query("""
            SELECT jc FROM JobCategoryJpaEntity jc
            WHERE jc.depth <= :depth
              AND jc.isActive = true
            ORDER BY jc.depth, jc.parentId, jc.sortOrder
            """)
    List<JobCategoryJpaEntity> findAllWithinDepth(@Param("depth") Integer depth);

    @Query("""
            SELECT jc FROM JobCategoryJpaEntity jc
            WHERE jc.categoryCode = :categoryCode
              AND jc.isActive = true
            """)
    Optional<JobCategoryJpaEntity> findActiveByCategoryCode(@Param("categoryCode") String categoryCode);

    Optional<JobCategoryJpaEntity> findById(Long id);

    // leaf → root 계층 경로 단일 SQL 조회 (결과 순서 root → leaf)
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

    // 여러 leaf 의 계층 경로 일괄 조회 (start_leaf_id 로 그룹핑, 그룹 내 root → leaf)
    @Query(value = """
            WITH RECURSIVE category_path (
                start_leaf_id, id, parent_id, category_code, category_name, depth,
                is_active, is_assignable, allows_custom_input, sort_order,
                created_at, updated_at
            ) AS (
                SELECT id AS start_leaf_id, id, parent_id, category_code, category_name, depth,
                       is_active, is_assignable, allows_custom_input, sort_order,
                       created_at, updated_at
                  FROM job_category
                 WHERE id IN (:leafIds)
                UNION ALL
                SELECT cp.start_leaf_id, jc.id, jc.parent_id, jc.category_code, jc.category_name, jc.depth,
                       jc.is_active, jc.is_assignable, jc.allows_custom_input, jc.sort_order,
                       jc.created_at, jc.updated_at
                  FROM job_category jc
                  JOIN category_path cp ON cp.parent_id = jc.id
            )
            SELECT start_leaf_id AS startLeafId,
                   id, parent_id AS parentId, category_code AS categoryCode, category_name AS categoryName, depth,
                   is_active AS isActive, is_assignable AS isAssignable, allows_custom_input AS allowsCustomInput, sort_order AS sortOrder,
                   created_at AS createdAt, updated_at AS updatedAt
              FROM category_path
             ORDER BY start_leaf_id, depth ASC
            """, nativeQuery = true)
    List<JobCategoryHierarchyRow> findHierarchyPathsByLeafIds(@Param("leafIds") Collection<Long> leafIds);
}

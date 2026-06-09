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

    /**
     * 주어진 여러 leaf id 들 각각의 카테고리부터 root 까지의 계층 경로를 단일 SQL 로 일괄 조회한다.
     *
     * <p>각 행에는 시작 leaf 를 가리키는 {@code start_leaf_id} 컬럼이 동봉되어, 어댑터가 이를
     * 키로 그룹핑하면 {@code Map<Long, List<...>>} 형태로 변환할 수 있다. 결과는
     * {@code start_leaf_id, depth ASC} 로 정렬되어 그룹 내 순서가 root → leaf 로 보장된다.</p>
     */
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

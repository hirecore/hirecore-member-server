package io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.entity.JobCategoryJpaEntity;
import io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.mapper.JobCategoryJpaEntityMapperImpl;
import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.JpaAuditingConfig;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.QueryDslConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link JobCategoryJpaQueryAdapter} 통합 테스트.
 *
 * <p>JPQL 의 다음 동작을 검증합니다.
 * <ul>
 *     <li>{@code depth <= maxDepth} 범위로 필터링</li>
 *     <li>{@code is_active = false} 인 항목 제외</li>
 *     <li>{@code depth → parentId → sortOrder} 순으로 정렬</li>
 * </ul>
 * </p>
 */
@DisplayName("JobCategoryJpaQueryAdapter 통합 테스트")
@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@Import({
        JpaAuditingConfig.class,
        QueryDslConfig.class,
        JobCategoryJpaQueryAdapter.class,
        JobCategoryJpaEntityMapperImpl.class
})
class JobCategoryJpaQueryAdapterTest {

    @Autowired
    private JobCategoryJpaQueryAdapter adapter;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private JobCategoryJpaEntity insert(
            Long id,
            Long parentId,
            Integer depth,
            Integer sortOrder,
            boolean isActive
    ) {
        Instant now = Instant.now();
        JobCategoryJpaEntity entity = JobCategoryJpaEntity.builder()
                .id(id)
                .parentId(parentId)
                .categoryCode("CODE_" + id)
                .categoryName("NAME_" + id)
                .depth(depth)
                .isActive(isActive)
                .isAssignable(true)
                .allowsCustomInput(false)
                .sortOrder(sortOrder)
                .auditingInfo(new AuditingJpaInfo(now, now))
                .build();
        entityManager.persist(entity);
        return entity;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("loadAllWithinDepth")
    class LoadAllWithinDepthTest {

        @Test
        @DisplayName("max-depth=2 이면 depth 1, 2 노드만 반환하고 depth 3 노드는 제외한다")
        void should_filter_by_depth_upper_bound() {
            // given: depth 1, 2, 3 각각 1건씩
            insert(1L, null, 1, 1, true);
            insert(11L, 1L, 2, 1, true);
            insert(111L, 11L, 3, 1, true);
            flushAndClear();

            // when
            List<JobCategory> result = adapter.loadAllWithinDepth(2);

            // then
            assertThat(result).extracting(JobCategory::getId)
                    .containsExactly(1L, 11L);
        }

        @Test
        @DisplayName("is_active=false 인 카테고리는 결과에서 제외한다")
        void should_exclude_inactive_categories() {
            // given
            insert(1L, null, 1, 1, true);
            insert(2L, null, 1, 2, false);
            flushAndClear();

            // when
            List<JobCategory> result = adapter.loadAllWithinDepth(3);

            // then
            assertThat(result).extracting(JobCategory::getId)
                    .containsExactly(1L);
        }

        @Test
        @DisplayName("결과는 depth → parentId → sortOrder 오름차순으로 정렬된다")
        void should_order_by_depth_then_parent_id_then_sort_order() {
            // given: 일부러 ID 와 입력 순서를 정렬과 어긋나게 삽입
            insert(22L, 2L, 2, 1, true);
            insert(2L, null, 1, 2, true);
            insert(1L, null, 1, 1, true);
            insert(11L, 1L, 2, 2, true);
            insert(12L, 1L, 2, 1, true);
            insert(21L, 2L, 2, 2, true);
            flushAndClear();

            // when
            List<JobCategory> result = adapter.loadAllWithinDepth(3);

            // then: depth1 (sortOrder asc) → depth2 (parentId asc, 그 안에서 sortOrder asc)
            assertThat(result).extracting(JobCategory::getId)
                    .containsExactly(
                            1L,   // depth=1, sortOrder=1
                            2L,   // depth=1, sortOrder=2
                            12L,  // depth=2, parentId=1, sortOrder=1
                            11L,  // depth=2, parentId=1, sortOrder=2
                            22L,  // depth=2, parentId=2, sortOrder=1
                            21L   // depth=2, parentId=2, sortOrder=2
                    );
        }

        @Test
        @DisplayName("저장된 카테고리가 없으면 빈 리스트를 반환한다")
        void should_return_empty_list_when_no_categories() {
            // when
            List<JobCategory> result = adapter.loadAllWithinDepth(3);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("loadHierarchyByLeafId — Recursive CTE")
    class LoadHierarchyByLeafIdTest {

        private Statistics statistics;

        @BeforeEach
        void resetStatistics() {
            statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
            statistics.clear();
        }

        @Test
        @DisplayName("leaf 부터 root 까지의 경로를 root → leaf 순서로 반환한다")
        void should_return_path_from_root_to_leaf() {
            // given: root(1) ← mid(11) ← leaf(111)
            insert(1L, null, 1, 1, true);
            insert(11L, 1L, 2, 1, true);
            insert(111L, 11L, 3, 1, true);
            flushAndClear();

            // when
            List<JobCategory> result = adapter.loadHierarchyByLeafId(111L);

            // then
            assertThat(result).extracting(JobCategory::getId).containsExactly(1L, 11L, 111L);
        }

        @Test
        @DisplayName("leaf 가 존재하지 않으면 빈 리스트를 반환한다")
        void should_return_empty_when_leaf_missing() {
            // when
            List<JobCategory> result = adapter.loadHierarchyByLeafId(9999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("계층 깊이와 무관하게 단일 SQL 만 발행한다 (Recursive CTE)")
        void should_emit_single_sql_regardless_of_depth() {
            // given: depth 4 짜리 트리
            insert(1L, null, 1, 1, true);
            insert(11L, 1L, 2, 1, true);
            insert(111L, 11L, 3, 1, true);
            insert(1111L, 111L, 4, 1, true);
            flushAndClear();
            statistics.clear(); // setup INSERT 카운트 제외

            // when
            List<JobCategory> result = adapter.loadHierarchyByLeafId(1111L);

            // then
            assertThat(result).hasSize(4);
            assertThat(statistics.getPrepareStatementCount())
                    .as("Recursive CTE 단일 쿼리로 leaf → root 전체 경로 로딩")
                    .isEqualTo(1L);
        }

        @Test
        @DisplayName("루트 노드 한 건만 있는 트리에서도 단일 쿼리로 1건 반환한다")
        void should_return_single_root_when_leaf_is_root() {
            // given
            insert(1L, null, 1, 1, true);
            flushAndClear();
            statistics.clear();

            // when
            List<JobCategory> result = adapter.loadHierarchyByLeafId(1L);

            // then
            assertThat(result).extracting(JobCategory::getId).containsExactly(1L);
            assertThat(statistics.getPrepareStatementCount()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("loadHierarchiesByLeafIds — 다중 leaf Recursive CTE")
    class LoadHierarchiesByLeafIdsTest {

        private Statistics statistics;

        @BeforeEach
        void resetStatistics() {
            statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
            statistics.clear();
        }

        @Test
        @DisplayName("여러 leaf 의 root → leaf 경로를 단일 SQL 로 일괄 반환한다 (Map key = leaf id)")
        void should_return_all_paths_in_single_sql() {
            // given: 두 트리 — root(1)←mid(11)←leaf(111), root(2)←leaf(22)
            insert(1L, null, 1, 1, true);
            insert(11L, 1L, 2, 1, true);
            insert(111L, 11L, 3, 1, true);
            insert(2L, null, 1, 1, true);
            insert(22L, 2L, 2, 1, true);
            flushAndClear();
            statistics.clear();

            // when
            Map<Long, List<JobCategory>> result = adapter.loadHierarchiesByLeafIds(List.of(111L, 22L));

            // then
            assertThat(result).containsOnlyKeys(111L, 22L);
            assertThat(result.get(111L)).extracting(JobCategory::getId).containsExactly(1L, 11L, 111L);
            assertThat(result.get(22L)).extracting(JobCategory::getId).containsExactly(2L, 22L);
            assertThat(statistics.getPrepareStatementCount())
                    .as("다중 leaf 일괄 CTE → 단일 SQL")
                    .isEqualTo(1L);
        }

        @Test
        @DisplayName("입력 leaf 개수와 무관하게 단일 SQL 만 발행한다 (K 비례 차단)")
        void should_emit_single_sql_regardless_of_k() {
            // given: 5개의 독립 트리, 각 depth 2
            for (int i = 1; i <= 5; i++) {
                long rootId = i;
                long leafId = i * 10L;
                insert(rootId, null, 1, 1, true);
                insert(leafId, rootId, 2, 1, true);
            }
            flushAndClear();
            statistics.clear();

            // when - K=5
            Map<Long, List<JobCategory>> result = adapter.loadHierarchiesByLeafIds(List.of(10L, 20L, 30L, 40L, 50L));

            // then
            assertThat(result).hasSize(5);
            assertThat(statistics.getPrepareStatementCount()).isEqualTo(1L);
        }

        @Test
        @DisplayName("존재하지 않는 leaf 는 결과 Map 에서 제외된다")
        void should_skip_missing_leaves() {
            // given
            insert(1L, null, 1, 1, true);
            insert(11L, 1L, 2, 1, true);
            flushAndClear();
            statistics.clear();

            // when - 11L 은 존재, 9999L 은 없음
            Map<Long, List<JobCategory>> result = adapter.loadHierarchiesByLeafIds(List.of(11L, 9999L));

            // then
            assertThat(result).containsOnlyKeys(11L);
            assertThat(result.get(11L)).extracting(JobCategory::getId).containsExactly(1L, 11L);
        }

        @Test
        @DisplayName("입력이 비어 있으면 SQL 을 발행하지 않고 빈 Map 을 반환한다")
        void should_not_emit_sql_when_input_is_empty() {
            // when
            Map<Long, List<JobCategory>> result = adapter.loadHierarchiesByLeafIds(List.of());

            // then
            assertThat(result).isEmpty();
            assertThat(statistics.getPrepareStatementCount()).isZero();
        }
    }
}

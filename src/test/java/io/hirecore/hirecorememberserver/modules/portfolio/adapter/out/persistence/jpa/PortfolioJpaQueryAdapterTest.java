package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioContentJpaEntityMapperImpl;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioJobCategoryJpaEntityMapperImpl;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioJpaEntityMapperImpl;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioTagJpaEntityMapperImpl;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.JpaAuditingConfig;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.QueryDslConfig;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link PortfolioJpaQueryAdapter} 통합 테스트 — sub-aggregate fetch 전략 검증.
 *
 * <p>{@code findById} / {@code findAllPublicByMemberAccountIdExcludingOrderByUpdatedAtDesc} 호출 시
 * sub-aggregate(content/jobCategory/tags) 일괄 로딩으로 발행 SQL 수가 결과 개수와 무관하게
 * 상수(2) 로 유지되는지를 Hibernate {@link Statistics} 로 측정한다.</p>
 */
@DisplayName("PortfolioJpaQueryAdapter 통합 테스트 - sub-aggregate fetch 전략")
@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@Import({
        JpaAuditingConfig.class,
        QueryDslConfig.class,
        PortfolioJpaQueryAdapter.class,
        PortfolioJpaCommandAdapter.class,
        PortfolioJpaEntityMapperImpl.class,
        PortfolioContentJpaEntityMapperImpl.class,
        PortfolioJobCategoryJpaEntityMapperImpl.class,
        PortfolioTagJpaEntityMapperImpl.class
})
class PortfolioJpaQueryAdapterTest {

    @Autowired private PortfolioJpaQueryAdapter queryAdapter;
    @Autowired private PortfolioJpaCommandAdapter commandAdapter;
    @Autowired private EntityManager entityManager;
    @Autowired private EntityManagerFactory entityManagerFactory;

    private Statistics statistics;

    @BeforeEach
    void resetStatistics() {
        statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();
    }

    private Portfolio saveSamplePortfolio(Long ownerId, Visibility visibility, List<String> tagNames) {
        List<PortfolioTag> tags = IntStream.range(0, tagNames.size())
                .mapToObj(i -> PortfolioTag.create(tagNames.get(i), i))
                .toList();
        Portfolio portfolio = Portfolio.create(
                ownerId,
                null,
                null,
                null,
                "타이틀",
                "프리뷰",
                null,
                9L,
                null,
                "{\"type\":\"doc\"}",
                "<p>본문</p>",
                List.of(),
                List.of(),
                tags,
                CollaborationType.PERSONAL,
                visibility
        );
        commandAdapter.save(portfolio);
        entityManager.flush();
        entityManager.clear();
        return portfolio;
    }

    @Nested
    @DisplayName("findPortfolio (단건 조회)")
    class FindByIdQueryCountTest {

        @Test
        @DisplayName("sub-aggregate (content/jobCategory/tags) 가 한 호출 안에 모두 로딩된다")
        void should_load_all_sub_aggregates() {
            // given
            Portfolio saved = saveSamplePortfolio(1L, Visibility.PUBLIC, List.of("Spring", "JPA", "DDD"));

            // when
            Optional<Portfolio> loaded = queryAdapter.findById(saved.getId());

            // then
            assertThat(loaded).isPresent();
            Portfolio result = loaded.get();
            assertThat(result.getPortfolioContent()).isNotNull();
            assertThat(result.getPortfolioJobCategory()).isNotNull();
            assertThat(result.getPortfolioTags()).hasSize(3);
        }

        @Test
        @DisplayName("ToOne 두 자식은 fetch join 1쿼리, ToMany tags 는 BatchSize 1쿼리 — 총 2쿼리만 발행한다")
        void should_emit_two_sql_statements() {
            // given
            Portfolio saved = saveSamplePortfolio(1L, Visibility.PUBLIC, List.of("Spring", "JPA", "DDD"));
            statistics.clear();

            // when
            Optional<Portfolio> loaded = queryAdapter.findById(saved.getId());

            // then
            assertThat(loaded).isPresent();
            assertThat(statistics.getPrepareStatementCount())
                    .as("findById + sub-aggregate 일괄 로딩으로 2쿼리 (메인 1 + tags BatchSize 1)")
                    .isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("findAllPublicByMemberAccountIdExcludingOrderByUpdatedAtDesc (리스트 조회, N+1 차단)")
    class FindAllPublicExcludingQueryCountTest {

        @Test
        @DisplayName("같은 작성자의 다른 PUBLIC 작품 K건에 대해 K와 무관하게 2쿼리만 발행한다")
        void should_emit_two_sql_statements_regardless_of_k() {
            // given - 본 포트폴리오 제외 4건 (tag 개수 다양)
            Long ownerId = 1L;
            Portfolio target = saveSamplePortfolio(ownerId, Visibility.PUBLIC, List.of("X"));
            saveSamplePortfolio(ownerId, Visibility.PUBLIC, List.of("A", "B"));
            saveSamplePortfolio(ownerId, Visibility.PUBLIC, List.of("C"));
            saveSamplePortfolio(ownerId, Visibility.PUBLIC, List.of("D", "E", "F"));
            saveSamplePortfolio(ownerId, Visibility.PUBLIC, List.of());
            statistics.clear();

            // when
            List<Portfolio> others = queryAdapter
                    .findAllPublicByMemberAccountIdExcludingOrderByUpdatedAtDesc(ownerId, target.getId());

            // then
            assertThat(others).hasSize(4);
            others.forEach(p -> {
                assertThat(p.getPortfolioContent()).isNotNull();
                assertThat(p.getPortfolioJobCategory()).isNotNull();
            });
            assertThat(statistics.getPrepareStatementCount())
                    .as("리스트 fetch join 1쿼리 + 전체 tags BatchSize 일괄 로딩 1쿼리")
                    .isEqualTo(2L);
        }

        @Test
        @DisplayName("PRIVATE 작품과 자기 자신은 제외하고 PUBLIC 만 반환한다")
        void should_exclude_self_and_private() {
            // given
            Long ownerId = 1L;
            Portfolio target = saveSamplePortfolio(ownerId, Visibility.PUBLIC, List.of());
            Portfolio publicOther = saveSamplePortfolio(ownerId, Visibility.PUBLIC, List.of());
            saveSamplePortfolio(ownerId, Visibility.PRIVATE, List.of());

            // when
            List<Portfolio> others = queryAdapter
                    .findAllPublicByMemberAccountIdExcludingOrderByUpdatedAtDesc(ownerId, target.getId());

            // then
            assertThat(others).extracting(Portfolio::getId).containsExactly(publicOther.getId());
        }
    }
}

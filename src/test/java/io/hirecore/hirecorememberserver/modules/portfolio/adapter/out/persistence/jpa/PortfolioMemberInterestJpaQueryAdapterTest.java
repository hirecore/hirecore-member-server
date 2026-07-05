package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioContentJpaEntityMapperImpl;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioJobCategoryJpaEntityMapperImpl;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioJpaEntityMapperImpl;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioMemberInterestJpaEntityMapperImpl;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioTagJpaEntityMapperImpl;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioMemberInterest;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.JpaAuditingConfig;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.QueryDslConfig;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link PortfolioMemberInterestJpaQueryAdapter} 통합 테스트 — 관심 배치 조회 쿼리 검증.
 *
 * <p>{@code findInterestedPortfolioIds} 가 주어진 포트폴리오들 중 해당 회원이 관심 등록한 것만
 * 단일 쿼리로 정확히 반환하는지를 실제 영속성으로 확인한다(공개 목록의 isInterested 배치 조립 근거).</p>
 */
@DisplayName("PortfolioMemberInterestJpaQueryAdapter 통합 테스트 - 관심 배치 조회")
@DataJpaTest
@Import({
        JpaAuditingConfig.class,
        QueryDslConfig.class,
        PortfolioMemberInterestJpaQueryAdapter.class,
        PortfolioMemberInterestJpaCommandAdapter.class,
        PortfolioMemberInterestJpaEntityMapperImpl.class,
        PortfolioJpaCommandAdapter.class,
        PortfolioJpaEntityMapperImpl.class,
        PortfolioContentJpaEntityMapperImpl.class,
        PortfolioJobCategoryJpaEntityMapperImpl.class,
        PortfolioTagJpaEntityMapperImpl.class
})
class PortfolioMemberInterestJpaQueryAdapterTest {

    @Autowired private PortfolioMemberInterestJpaQueryAdapter queryAdapter;
    @Autowired private PortfolioMemberInterestJpaCommandAdapter interestCommandAdapter;
    @Autowired private PortfolioJpaCommandAdapter portfolioCommandAdapter;
    @Autowired private EntityManager entityManager;

    private Portfolio savePortfolio(Long ownerId) {
        Portfolio portfolio = Portfolio.create(
                ownerId,
                null,
                null,
                null,
                "포트폴리오 제목",
                "미리보기 요약",
                null,
                9001L,
                null,
                "{\"type\":\"doc\"}",
                "<p>본문</p>",
                List.of(),
                List.of(),
                List.of(),
                CollaborationType.PERSONAL,
                Visibility.PUBLIC
        );
        return portfolioCommandAdapter.save(portfolio);
    }

    private void saveInterest(Long portfolioId, Long memberAccountId) {
        interestCommandAdapter.save(portfolioId, PortfolioMemberInterest.create(memberAccountId));
    }

    @Test
    @DisplayName("주어진 포트폴리오들 중 해당 회원이 관심 등록한 ID만 반환한다 (다른 회원의 관심은 제외)")
    void should_return_only_interested_ids_for_viewer() {
        // given
        Long viewer = 500L;
        Portfolio p1 = savePortfolio(101L);
        Portfolio p2 = savePortfolio(102L);
        Portfolio p3 = savePortfolio(103L);
        entityManager.flush();

        saveInterest(p1.getId(), viewer);
        saveInterest(p3.getId(), viewer);
        saveInterest(p2.getId(), 999L); // 다른 회원의 관심 — 제외돼야 함
        entityManager.flush();
        entityManager.clear();

        // when
        Set<Long> result = queryAdapter.findInterestedPortfolioIds(
                List.of(p1.getId(), p2.getId(), p3.getId()), viewer);

        // then
        assertThat(result).containsExactlyInAnyOrder(p1.getId(), p3.getId());
    }

    @Test
    @DisplayName("입력 포트폴리오 목록에 없는 것은 관심 등록돼 있어도 반환하지 않는다")
    void should_restrict_to_given_portfolio_ids() {
        // given
        Long viewer = 500L;
        Portfolio inPage = savePortfolio(101L);
        Portfolio outOfPage = savePortfolio(102L);
        entityManager.flush();

        saveInterest(inPage.getId(), viewer);
        saveInterest(outOfPage.getId(), viewer);
        entityManager.flush();
        entityManager.clear();

        // when
        Set<Long> result = queryAdapter.findInterestedPortfolioIds(List.of(inPage.getId()), viewer);

        // then
        assertThat(result).containsExactly(inPage.getId());
    }

    @Test
    @DisplayName("빈 입력이면 쿼리 없이 빈 집합을 반환한다")
    void should_return_empty_for_empty_input() {
        assertThat(queryAdapter.findInterestedPortfolioIds(List.of(), 500L)).isEmpty();
    }
}

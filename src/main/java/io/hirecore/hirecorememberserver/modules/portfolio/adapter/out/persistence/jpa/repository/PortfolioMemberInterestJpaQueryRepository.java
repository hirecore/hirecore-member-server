package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioMemberInterestJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Set;

public interface PortfolioMemberInterestJpaQueryRepository extends Repository<PortfolioMemberInterestJpaEntity, Long> {
    boolean existsByPortfolio_IdAndMemberAccountId(Long portfolioId, Long memberAccountId);

    // 회원이 관심 등록한 포트폴리오 ID만 단일 쿼리로 조회 (목록 배치용)
    @Query("select i.portfolio.id from PortfolioMemberInterestJpaEntity i "
            + "where i.memberAccountId = :memberAccountId and i.portfolio.id in :portfolioIds")
    Set<Long> findInterestedPortfolioIds(@Param("memberAccountId") Long memberAccountId,
                                         @Param("portfolioIds") Collection<Long> portfolioIds);
}

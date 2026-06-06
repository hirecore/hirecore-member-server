package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioMemberInterestJpaEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface PortfolioMemberInterestJpaCommandRepository extends Repository<PortfolioMemberInterestJpaEntity, Long> {
    PortfolioMemberInterestJpaEntity save(PortfolioMemberInterestJpaEntity entity);

    @Modifying
    @Query("""
            DELETE FROM PortfolioMemberInterestJpaEntity i
             WHERE i.portfolio.id = :portfolioId
               AND i.memberAccountId = :memberAccountId
            """)
    int deleteByPortfolioIdAndMemberAccountId(
            @Param("portfolioId") Long portfolioId,
            @Param("memberAccountId") Long memberAccountId
    );

    @Modifying
    @Query("""
            DELETE FROM PortfolioMemberInterestJpaEntity i
             WHERE i.portfolio.id = :portfolioId
            """)
    int deleteAllByPortfolioId(@Param("portfolioId") Long portfolioId);
}

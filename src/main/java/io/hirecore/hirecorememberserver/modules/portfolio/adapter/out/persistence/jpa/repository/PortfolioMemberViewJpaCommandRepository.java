package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioMemberViewJpaEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface PortfolioMemberViewJpaCommandRepository extends Repository<PortfolioMemberViewJpaEntity, Long> {
    PortfolioMemberViewJpaEntity save(PortfolioMemberViewJpaEntity entity);

    @Modifying
    @Query("""
            DELETE FROM PortfolioMemberViewJpaEntity v
             WHERE v.portfolio.id = :portfolioId
            """)
    int deleteAllByPortfolioId(@Param("portfolioId") Long portfolioId);
}

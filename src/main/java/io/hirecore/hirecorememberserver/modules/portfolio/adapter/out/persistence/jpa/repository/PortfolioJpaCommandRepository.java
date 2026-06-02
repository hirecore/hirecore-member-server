package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface PortfolioJpaCommandRepository extends Repository<PortfolioJpaEntity, Long> {
    PortfolioJpaEntity save(PortfolioJpaEntity portfolio);

    @Modifying
    @Query("""
            UPDATE PortfolioJpaEntity p
               SET p.cachedViewCount = p.cachedViewCount + 1
             WHERE p.id = :id
            """)
    int incrementCachedViewCountById(@Param("id") Long id);

    @Modifying
    @Query("""
            UPDATE PortfolioJpaEntity p
               SET p.cachedInterestCount = p.cachedInterestCount + 1
             WHERE p.id = :id
            """)
    int incrementCachedInterestCountById(@Param("id") Long id);

    @Modifying
    @Query("""
            UPDATE PortfolioJpaEntity p
               SET p.cachedInterestCount = p.cachedInterestCount - 1
             WHERE p.id = :id
               AND p.cachedInterestCount > 0
            """)
    int decrementCachedInterestCountById(@Param("id") Long id);
}

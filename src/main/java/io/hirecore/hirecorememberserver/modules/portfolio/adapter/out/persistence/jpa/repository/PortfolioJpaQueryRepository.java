package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.projection.PortfolioTagProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfolioJpaQueryRepository extends Repository<PortfolioJpaEntity, Long> {
    boolean existsByIdAndMemberAccountId(Long id, Long memberAccountId);
    Optional<PortfolioJpaEntity> findById(Long id);

    @Query("""
        SELECT t.userInputTag, t.sortOrder
        FROM PortfolioTagJpaEntity t
        WHERE t.portfolio.id = :portfolioId
          AND t.isDeleted = false
        ORDER BY t.sortOrder ASC
    """)
    List<PortfolioTagProjection> findTagsByPortfolioId(@Param("portfolioId") Long portfolioId);
}

package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfolioJpaQueryRepository extends Repository<PortfolioJpaEntity, Long> {
    boolean existsByIdAndMemberAccountId(Long id, Long memberAccountId);
    Optional<PortfolioJpaEntity> findById(Long id);

    @Query("""
            SELECT p
              FROM PortfolioJpaEntity p
             WHERE p.memberAccountId = :memberAccountId
             ORDER BY p.auditingInfo.updatedAt DESC
            """)
    List<PortfolioJpaEntity> findAllByMemberAccountIdOrderByUpdatedAtDesc(@Param("memberAccountId") Long memberAccountId);

    @Query("""
            SELECT p
              FROM PortfolioJpaEntity p
             WHERE p.memberAccountId = :memberAccountId
               AND p.visibility = io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility.PUBLIC
               AND p.id <> :excludedPortfolioId
             ORDER BY p.auditingInfo.updatedAt DESC
            """)
    List<PortfolioJpaEntity> findAllPublicByMemberAccountIdExcludingOrderByUpdatedAtDesc(
            @Param("memberAccountId") Long memberAccountId,
            @Param("excludedPortfolioId") Long excludedPortfolioId
    );
}

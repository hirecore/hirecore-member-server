package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface PortfolioJpaQueryRepository extends Repository<PortfolioJpaEntity, Long> {
    boolean existsByIdAndMemberAccountId(Long id, Long memberAccountId);
    Optional<PortfolioJpaEntity> findById(Long id);
}

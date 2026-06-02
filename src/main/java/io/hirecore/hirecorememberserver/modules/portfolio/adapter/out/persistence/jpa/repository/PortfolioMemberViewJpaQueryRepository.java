package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioMemberViewJpaEntity;
import org.springframework.data.repository.Repository;

public interface PortfolioMemberViewJpaQueryRepository extends Repository<PortfolioMemberViewJpaEntity, Long> {
    boolean existsByPortfolio_IdAndMemberAccountId(Long portfolioId, Long memberAccountId);
}

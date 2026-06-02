package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioMemberInterestJpaEntity;
import org.springframework.data.repository.Repository;

public interface PortfolioMemberInterestJpaQueryRepository extends Repository<PortfolioMemberInterestJpaEntity, Long> {
    boolean existsByPortfolio_IdAndMemberAccountId(Long portfolioId, Long memberAccountId);
}

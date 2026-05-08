package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioTagJpaEntity;
import org.springframework.data.repository.Repository;

public interface PortfolioTagJpaCommandRepository extends Repository<PortfolioTagJpaEntity, Long> {
    PortfolioTagJpaEntity save(PortfolioTagJpaEntity portfolioTag);
}

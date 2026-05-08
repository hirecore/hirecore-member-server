package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import org.springframework.data.repository.Repository;

public interface PortfolioJpaCommandRepository extends Repository<PortfolioJpaEntity, Long> {
    PortfolioJpaEntity save(PortfolioJpaEntity portfolio);
}

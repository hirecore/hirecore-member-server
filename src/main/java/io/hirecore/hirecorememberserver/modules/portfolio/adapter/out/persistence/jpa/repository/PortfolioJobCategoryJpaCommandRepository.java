package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJobCategoryJpaEntity;
import org.springframework.data.repository.Repository;

public interface PortfolioJobCategoryJpaCommandRepository extends Repository<PortfolioJobCategoryJpaEntity, Long> {
    PortfolioJobCategoryJpaEntity save(PortfolioJobCategoryJpaEntity portfolioJobCategory);
}

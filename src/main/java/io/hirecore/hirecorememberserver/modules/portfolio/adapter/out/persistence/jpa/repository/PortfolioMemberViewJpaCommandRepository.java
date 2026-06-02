package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioMemberViewJpaEntity;
import org.springframework.data.repository.Repository;

public interface PortfolioMemberViewJpaCommandRepository extends Repository<PortfolioMemberViewJpaEntity, Long> {
    PortfolioMemberViewJpaEntity save(PortfolioMemberViewJpaEntity entity);
}

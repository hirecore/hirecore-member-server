package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioMemberInterestJpaEntity;
import org.springframework.data.repository.Repository;

public interface PortfolioMemberInterestJpaCommandRepository extends Repository<PortfolioMemberInterestJpaEntity, Long> {
    PortfolioMemberInterestJpaEntity save(PortfolioMemberInterestJpaEntity entity);
}

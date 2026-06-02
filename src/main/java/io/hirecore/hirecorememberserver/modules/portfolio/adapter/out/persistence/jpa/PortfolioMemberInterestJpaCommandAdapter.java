package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioMemberInterestJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioMemberInterestJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioMemberInterestJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.SavePortfolioMemberInterestPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioMemberInterest;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PortfolioMemberInterestJpaCommandAdapter implements SavePortfolioMemberInterestPort {

    private final EntityManager entityManager;
    private final PortfolioMemberInterestJpaCommandRepository repository;
    private final PortfolioMemberInterestJpaEntityMapper mapper;

    @Override
    public PortfolioMemberInterest save(Long portfolioId, PortfolioMemberInterest portfolioMemberInterest) {
        PortfolioMemberInterestJpaEntity entity = mapper.toJpaEntity(portfolioMemberInterest);
        PortfolioJpaEntity portfolioRef = entityManager.getReference(PortfolioJpaEntity.class, portfolioId);
        entity.attachPortfolio(portfolioRef);
        repository.save(entity);
        return portfolioMemberInterest;
    }
}

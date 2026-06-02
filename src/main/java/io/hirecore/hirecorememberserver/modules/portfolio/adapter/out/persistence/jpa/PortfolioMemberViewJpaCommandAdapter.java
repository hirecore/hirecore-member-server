package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioMemberViewJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioMemberViewJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioMemberViewJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.SavePortfolioMemberViewPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioMemberView;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PortfolioMemberViewJpaCommandAdapter implements SavePortfolioMemberViewPort {

    private final EntityManager entityManager;
    private final PortfolioMemberViewJpaCommandRepository repository;
    private final PortfolioMemberViewJpaEntityMapper mapper;

    @Override
    public PortfolioMemberView save(Long portfolioId, PortfolioMemberView portfolioMemberView) {
        PortfolioMemberViewJpaEntity entity = mapper.toJpaEntity(portfolioMemberView);
        PortfolioJpaEntity portfolioRef = entityManager.getReference(PortfolioJpaEntity.class, portfolioId);
        entity.attachPortfolio(portfolioRef);
        repository.save(entity);
        return portfolioMemberView;
    }
}

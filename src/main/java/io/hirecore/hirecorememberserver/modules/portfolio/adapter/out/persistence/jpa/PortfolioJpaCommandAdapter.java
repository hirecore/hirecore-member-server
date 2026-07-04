package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioMemberInterestJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioMemberViewJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.DecrementPortfolioInterestCountPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.DeletePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.IncrementPortfolioInterestCountPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.IncrementPortfolioViewCountPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.SavePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.UpdatePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

// 도메인 이벤트를 JPA 엔티티로 브리지하여 save 시 @DomainEvents 자동 발행에 위임
@Component
@RequiredArgsConstructor
public class PortfolioJpaCommandAdapter implements
        SavePortfolioPort,
        IncrementPortfolioViewCountPort,
        IncrementPortfolioInterestCountPort,
        DecrementPortfolioInterestCountPort,
        UpdatePortfolioPort,
        DeletePortfolioPort
{

    private final PortfolioJpaEntityMapper portfolioMapper;
    private final PortfolioJpaCommandRepository portfolioRepository;
    private final PortfolioMemberInterestJpaCommandRepository portfolioMemberInterestRepository;
    private final PortfolioMemberViewJpaCommandRepository portfolioMemberViewRepository;
    private final EntityManager entityManager;

    @Override
    public Portfolio save(Portfolio portfolio) {
        PortfolioJpaEntity portfolioEntity = portfolioMapper.toJpaEntity(portfolio);
        bridgeDomainEvents(portfolio, portfolioEntity);
        portfolioRepository.save(portfolioEntity);
        return portfolio;
    }

    @Override
    public void incrementViewCountById(Long portfolioId) {
        portfolioRepository.incrementCachedViewCountById(portfolioId);
    }

    @Override
    public void incrementInterestCountById(Long portfolioId) {
        portfolioRepository.incrementCachedInterestCountById(portfolioId);
    }

    @Override
    public void decrementInterestCountById(Long portfolioId) {
        portfolioRepository.decrementCachedInterestCountById(portfolioId);
    }

    // markPersisted로 merge 경로 진입, cascade+orphanRemoval로 자식 동기화
    @Override
    public void update(Portfolio portfolio) {
        PortfolioJpaEntity portfolioEntity = portfolioMapper.toJpaEntity(portfolio);
        bridgeDomainEvents(portfolio, portfolioEntity);
        portfolioEntity.markPersisted();
        portfolioRepository.save(portfolioEntity);
    }

    // 삭제 전 cascade 끊긴 자식(interests/views) 잔존 행을 명시 cleanup
    @Override
    public void delete(Portfolio portfolio) {
        Long portfolioId = portfolio.getId();
        portfolioMemberInterestRepository.deleteAllByPortfolioId(portfolioId);
        portfolioMemberViewRepository.deleteAllByPortfolioId(portfolioId);
        PortfolioJpaEntity managed = entityManager.find(PortfolioJpaEntity.class, portfolioId);
        if (managed != null) {
            portfolioRepository.delete(managed);
        }
    }

    private void bridgeDomainEvents(Portfolio domain, PortfolioJpaEntity entity) {
        Collection<Object> domainEvents = domain.pollAllEvents();
        if (domainEvents != null && !domainEvents.isEmpty()) {
            domainEvents.forEach(entity::recordPersistenceEvent);
        }
    }
}

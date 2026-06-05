package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.DecrementPortfolioInterestCountPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.IncrementPortfolioInterestCountPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.IncrementPortfolioViewCountPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.SavePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.UpdatePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PortfolioJpaCommandAdapter implements
        SavePortfolioPort,
        IncrementPortfolioViewCountPort,
        IncrementPortfolioInterestCountPort,
        DecrementPortfolioInterestCountPort,
        UpdatePortfolioPort
{

    private final PortfolioJpaEntityMapper portfolioMapper;
    private final PortfolioJpaCommandRepository portfolioRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Portfolio save(Portfolio portfolio) {
        PortfolioJpaEntity portfolioEntity = portfolioMapper.toJpaEntity(portfolio);
        portfolioRepository.save(portfolioEntity);
        portfolio.pollAllEvents().forEach(applicationEventPublisher::publishEvent);
        return portfolio;
    }

    @Override
    public void incrementById(Long portfolioId) {
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

    /**
     * 도메인 변경분을 새 JPA 엔티티 그래프로 변환한 뒤 {@code markPersisted()} 로 기존 영속 객체임을 표시하고
     * {@code save()} 의 merge 경로로 진입시킵니다. cascade + orphanRemoval 로 자식 컬렉션은 자동 동기화됩니다.
     */
    @Override
    public void update(Portfolio portfolio) {
        PortfolioJpaEntity portfolioEntity = portfolioMapper.toJpaEntity(portfolio);
        portfolioEntity.markPersisted();
        portfolioRepository.save(portfolioEntity);
        portfolio.pollAllEvents().forEach(applicationEventPublisher::publishEvent);
    }
}

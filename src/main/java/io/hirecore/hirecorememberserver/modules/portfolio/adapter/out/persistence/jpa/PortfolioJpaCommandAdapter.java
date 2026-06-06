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

/**
 * 도메인 POJO 의 이벤트를 JPA 엔티티로 이전(Event Bridge) 한 뒤
 * {@code repository.save()} 호출 시 Spring Data 가 {@code @DomainEvents} 를 통해
 * 자동으로 이벤트를 발행하도록 위임한다.
 */
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
        bridgeDomainEvents(portfolio, portfolioEntity);
        portfolioEntity.markPersisted();
        portfolioRepository.save(portfolioEntity);
    }

    /**
     * 포트폴리오 본체 + cascade 자식(contents/job_categories/tags) 영구 삭제 전에,
     * AR 매핑에서 분리되어 cascade 가 끊긴 두 자식 테이블
     * ({@code portfolio_member_interests}, {@code portfolio_member_views}) 의
     * 잔존 행을 명시적으로 cleanup 한다.
     *
     * <p>{@code Portfolio.delete()} 가 발행한 도메인 이벤트는 use case 가 {@code PublishDomainEventsPort} 로
     * 명시 발행하므로, 본 메서드는 영속화 책임에만 집중한다.</p>
     */
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

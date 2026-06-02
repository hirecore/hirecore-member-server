package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioContentJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJobCategoryJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioTagJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioContentJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioJobCategoryJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioTagJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.IncrementPortfolioViewCountPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.SavePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.UpdatePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PortfolioJpaCommandAdapter implements SavePortfolioPort, IncrementPortfolioViewCountPort, UpdatePortfolioPort {

    private final PortfolioJpaEntityMapper portfolioMapper;
    private final PortfolioContentJpaEntityMapper portfolioContentMapper;
    private final PortfolioJobCategoryJpaEntityMapper portfolioJobCategoryMapper;
    private final PortfolioTagJpaEntityMapper portfolioTagMapper;
    private final PortfolioJpaCommandRepository portfolioRepository;

    @Override
    public Portfolio save(Portfolio portfolio) {
        PortfolioJpaEntity portfolioEntity = portfolioMapper.toJpaEntity(portfolio);

        PortfolioContentJpaEntity contentEntity = portfolioContentMapper.toJpaEntity(portfolio.getPortfolioContent());
        portfolioEntity.syncPortfolioContent(contentEntity);

        PortfolioJobCategoryJpaEntity jobCategoryEntity = portfolioJobCategoryMapper.toJpaEntity(portfolio.getPortfolioJobCategory());
        portfolioEntity.syncPortfolioJobCategory(jobCategoryEntity);

        for (PortfolioTagJpaEntity tagEntity : portfolio.getPortfolioTags().stream().map(portfolioTagMapper::toJpaEntity).toList()) {
            portfolioEntity.addPortfolioTag(tagEntity);
        }

        portfolioRepository.save(portfolioEntity);
        return portfolio;
    }

    @Override
    public void incrementById(Long portfolioId) {
        portfolioRepository.incrementCachedViewCountById(portfolioId);
    }

    /**
     * 도메인 변경분을 새 JPA 엔티티로 변환한 뒤 {@code markPersisted()} 로 기존 영속 객체임을 표시하고
     * {@code save()} 의 merge 경로로 진입시킵니다. cascade + orphanRemoval 로 자식 컬렉션은 자동 동기화됩니다.
     */
    @Override
    public void update(Portfolio portfolio) {
        PortfolioJpaEntity portfolioEntity = portfolioMapper.toJpaEntity(portfolio);

        PortfolioContentJpaEntity contentEntity = portfolioContentMapper.toJpaEntity(portfolio.getPortfolioContent());
        portfolioEntity.syncPortfolioContent(contentEntity);

        PortfolioJobCategoryJpaEntity jobCategoryEntity = portfolioJobCategoryMapper.toJpaEntity(portfolio.getPortfolioJobCategory());
        portfolioEntity.syncPortfolioJobCategory(jobCategoryEntity);

        for (PortfolioTagJpaEntity tagEntity : portfolio.getPortfolioTags().stream().map(portfolioTagMapper::toJpaEntity).toList()) {
            portfolioEntity.addPortfolioTag(tagEntity);
        }

        portfolioEntity.markPersisted();
        portfolioRepository.save(portfolioEntity);
    }
}

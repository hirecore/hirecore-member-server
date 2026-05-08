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
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.SavePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PortfolioJpaCommandAdapter implements SavePortfolioPort {

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
        portfolioEntity.addPortfolioJobCategory(jobCategoryEntity);

        for (PortfolioTagJpaEntity tagEntity : portfolio.getPortfolioTags().stream().map(portfolioTagMapper::toJpaEntity).toList()) {
            portfolioEntity.addPortfolioTag(tagEntity);
        }

        portfolioRepository.save(portfolioEntity);
        return portfolio;
    }
}

package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioContentJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJobCategoryJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioTagJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioJobCategoryJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioTagJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.SavePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PortfolioJpaCommandAdapter implements SavePortfolioPort {

    private final PortfolioJpaEntityMapper mapper;
    private final PortfolioJpaCommandRepository portfolioRepository;
    private final PortfolioJobCategoryJpaCommandRepository portfolioJobCategoryRepository;
    private final PortfolioTagJpaCommandRepository portfolioTagRepository;

    @Override
    public Portfolio save(Portfolio portfolio) {
        PortfolioJpaEntity portfolioEntity = mapper.toJpaEntity(portfolio);
        PortfolioContentJpaEntity contentEntity = mapper.toContentJpaEntity(portfolio.getPortfolioContent());
        portfolioEntity.syncPortfolioContent(contentEntity);

        PortfolioJpaEntity savedPortfolio = portfolioRepository.save(portfolioEntity);

        PortfolioJobCategoryJpaEntity jobCategoryEntity = mapper.toJobCategoryJpaEntity(portfolio.getPortfolioJobCategory());
        jobCategoryEntity.attachPortfolio(savedPortfolio);
        portfolioJobCategoryRepository.save(jobCategoryEntity);

        List<PortfolioTagJpaEntity> tagEntities = portfolio.getPortfolioTags().stream()
                .map(mapper::toTagJpaEntity)
                .toList();
        for (PortfolioTagJpaEntity tagEntity : tagEntities) {
            tagEntity.attachPortfolio(savedPortfolio);
            portfolioTagRepository.save(tagEntity);
        }

        return portfolio;
    }
}

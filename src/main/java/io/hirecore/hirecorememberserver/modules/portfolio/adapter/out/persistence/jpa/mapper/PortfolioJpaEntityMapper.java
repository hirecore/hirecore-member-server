package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioContentJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJobCategoryJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioTagJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioContent;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioJobCategory;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class PortfolioJpaEntityMapper {

    @Mapping(target = "portfolioJobCategoryId", source = "portfolioJobCategory.id")
    public abstract PortfolioJpaEntity toJpaEntity(Portfolio domain);

    @Mapping(target = "id", source = "portfolioId")
    @Mapping(target = "portfolio", ignore = true)
    public abstract PortfolioContentJpaEntity toContentJpaEntity(PortfolioContent domain);

    @Mapping(target = "portfolio", ignore = true)
    public abstract PortfolioJobCategoryJpaEntity toJobCategoryJpaEntity(PortfolioJobCategory domain);

    @Mapping(target = "portfolio", ignore = true)
    public abstract PortfolioTagJpaEntity toTagJpaEntity(PortfolioTag domain);
}

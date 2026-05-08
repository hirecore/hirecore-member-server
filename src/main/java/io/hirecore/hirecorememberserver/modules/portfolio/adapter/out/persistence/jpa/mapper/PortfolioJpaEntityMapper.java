package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class PortfolioJpaEntityMapper {

    @Mapping(target = "portfolioJobCategoryId", source = "portfolioJobCategory.id")
    @Mapping(target = "portfolioContent", ignore = true)
    public abstract PortfolioJpaEntity toJpaEntity(Portfolio domain);
}

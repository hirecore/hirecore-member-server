package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioContentJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class PortfolioContentJpaEntityMapper {

    @Mapping(target = "id", source = "portfolioId")
    @Mapping(target = "portfolio", ignore = true)
    public abstract PortfolioContentJpaEntity toJpaEntity(PortfolioContent domain);
}

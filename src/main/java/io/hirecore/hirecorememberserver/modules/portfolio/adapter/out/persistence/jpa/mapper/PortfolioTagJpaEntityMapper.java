package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioTagJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class PortfolioTagJpaEntityMapper {

    @Mapping(target = "portfolio", ignore = true)
    public abstract PortfolioTagJpaEntity toJpaEntity(PortfolioTag domain);
}

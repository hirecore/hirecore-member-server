package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioMemberViewJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioMemberView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class PortfolioMemberViewJpaEntityMapper {

    @Mapping(target = "portfolio", ignore = true)
    public abstract PortfolioMemberViewJpaEntity toJpaEntity(PortfolioMemberView domain);
}

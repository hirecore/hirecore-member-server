package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioMemberInterestJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioMemberInterest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class PortfolioMemberInterestJpaEntityMapper {

    @Mapping(target = "portfolio", ignore = true)
    public abstract PortfolioMemberInterestJpaEntity toJpaEntity(PortfolioMemberInterest domain);
}

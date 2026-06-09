package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJobCategoryJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioJobCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class PortfolioJobCategoryJpaEntityMapper {

    @Mapping(target = "portfolio", ignore = true)
    public abstract PortfolioJobCategoryJpaEntity toJpaEntity(PortfolioJobCategory domain);

    public PortfolioJobCategory toDomain(PortfolioJobCategoryJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return PortfolioJobCategory.builder()
                .id(entity.getId())
                .jobCategoryLeafId(entity.getJobCategoryLeafId())
                .userInput(entity.getUserInput())
                .connectedAt(entity.getConnectedAt())
                .build();
    }
}

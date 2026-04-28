package io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.entity.JobCategoryJpaEntity;
import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class JobCategoryJpaEntityMapper {
    public abstract JobCategoryJpaEntity toJpaEntity(JobCategory domain);
    public abstract JobCategory toDomain(JobCategoryJpaEntity jpaEntity);
}

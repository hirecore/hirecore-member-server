package io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.entity.ResumeJobCategoryJpaEntity;
import io.hirecore.hirecorememberserver.modules.resume.domain.ResumeJobCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class ResumeJobCategoryJpaEntityMapper {

    @Mapping(target = "resume", ignore = true)
    public abstract ResumeJobCategoryJpaEntity toJpaEntity(ResumeJobCategory domain);

    public ResumeJobCategory toDomain(ResumeJobCategoryJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ResumeJobCategory.builder()
                .id(entity.getId())
                .jobCategoryId(entity.getJobCategoryId())
                .userInput(entity.getUserInput())
                .connectedAt(entity.getConnectedAt())
                .build();
    }
}

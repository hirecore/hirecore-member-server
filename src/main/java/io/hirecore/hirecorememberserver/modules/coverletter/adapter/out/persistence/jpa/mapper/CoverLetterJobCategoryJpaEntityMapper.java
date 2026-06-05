package io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa.entity.CoverLetterJobCategoryJpaEntity;
import io.hirecore.hirecorememberserver.modules.coverletter.domain.CoverLetterJobCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class CoverLetterJobCategoryJpaEntityMapper {

    @Mapping(target = "coverLetter", ignore = true)
    public abstract CoverLetterJobCategoryJpaEntity toJpaEntity(CoverLetterJobCategory domain);

    public CoverLetterJobCategory toDomain(CoverLetterJobCategoryJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return CoverLetterJobCategory.builder()
                .id(entity.getId())
                .jobCategoryId(entity.getJobCategoryId())
                .userInput(entity.getUserInput())
                .connectedAt(entity.getConnectedAt())
                .build();
    }
}

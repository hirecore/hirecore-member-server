package io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.mapper.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.entity.ImageFileMetaJpaEntity;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class ImageFileMetaJpaEntityMapper {
    public abstract ImageFileMetaJpaEntity toJpaEntity(ImageFileMeta domain);
    public abstract ImageFileMeta toDomain(ImageFileMetaJpaEntity jpaEntity);
}

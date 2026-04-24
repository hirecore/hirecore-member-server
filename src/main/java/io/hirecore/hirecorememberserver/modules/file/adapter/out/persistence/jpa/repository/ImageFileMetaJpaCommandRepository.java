package io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.entity.ImageFileMetaJpaEntity;
import org.springframework.data.repository.Repository;

public interface ImageFileMetaJpaCommandRepository extends Repository<ImageFileMetaJpaEntity, Long> {
    ImageFileMetaJpaEntity save(ImageFileMetaJpaEntity imageFileMetaJpaEntity);
}

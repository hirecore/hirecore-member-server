package io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.entity.ImageFileMetaJpaEntity;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ImageFileMetaJpaQueryRepository extends Repository<ImageFileMetaJpaEntity, Long> {
    List<ImageFileMetaJpaEntity> findAllByIdIn(Collection<Long> ids);
    Optional<ImageFileMetaJpaEntity> findById(Long id);
}

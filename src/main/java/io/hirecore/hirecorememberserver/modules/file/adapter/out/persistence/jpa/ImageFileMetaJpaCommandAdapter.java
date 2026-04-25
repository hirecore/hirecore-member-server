package io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.entity.ImageFileMetaJpaEntity;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.mapper.ImageFileMetaJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.repository.ImageFileMetaJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.SaveImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class ImageFileMetaJpaCommandAdapter implements SaveImageFileMetaPort {

    private final ImageFileMetaJpaEntityMapper imageFileMetaJpaEntityMapper;
    private final ImageFileMetaJpaCommandRepository imageFileMetaJpaCommandRepository;

    @Override
    public ImageFileMeta save(ImageFileMeta imageFileMeta) {
        ImageFileMetaJpaEntity entity = imageFileMetaJpaEntityMapper.toJpaEntity(imageFileMeta);

        Collection<Object> domainEvents = imageFileMeta.pollAllEvents();
        if (domainEvents != null && !domainEvents.isEmpty()) {
            domainEvents.forEach(entity::recordPersistenceEvent);
        }

        ImageFileMetaJpaEntity savedEntity = imageFileMetaJpaCommandRepository.save(entity);
        return imageFileMetaJpaEntityMapper.toDomain(savedEntity);
    }
}

package io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.entity.ImageFileMetaJpaEntity;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.mapper.ImageFileMetaJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.repository.ImageFileMetaJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.repository.ImageFileMetaJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.SaveImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.UpdateImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.UploadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageFileMetaJpaCommandAdapter
        implements SaveImageFileMetaPort, UpdateImageFileMetaPort
{

    private final ImageFileMetaJpaEntityMapper imageFileMetaJpaEntityMapper;
    private final ImageFileMetaJpaCommandRepository imageFileMetaJpaCommandRepository;
    private final ImageFileMetaJpaQueryRepository imageFileMetaJpaQueryRepository;

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

    @Override
    public void markAllAsUploaded(Collection<Long> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) {
            return;
        }

        List<ImageFileMetaJpaEntity> entities = imageFileMetaJpaQueryRepository.findAllByIdIn(imageIds);
        Instant now = Instant.now();
        for (ImageFileMetaJpaEntity entity : entities) {
            entity.updateUploadStatus(UploadStatus.UPLOADED, now);
        }
    }
}

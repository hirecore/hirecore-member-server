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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public void markAllAsUploaded(List<ImageFileMeta> imageFileMetas) {
        if (imageFileMetas == null || imageFileMetas.isEmpty()) {
            return;
        }

        List<Long> ids = imageFileMetas.stream().map(ImageFileMeta::getId).toList();
        Map<Long, ImageFileMetaJpaEntity> entityById = imageFileMetaJpaQueryRepository
                .findAllByIdIn(ids)
                .stream()
                .collect(Collectors.toMap(ImageFileMetaJpaEntity::getId, Function.identity()));

        Instant now = Instant.now();
        for (ImageFileMeta domain : imageFileMetas) {
            ImageFileMetaJpaEntity entity = entityById.get(domain.getId());
            if (entity == null) {
                continue;
            }

            entity.updateUploadStatus(UploadStatus.UPLOADED, now);

            Collection<Object> domainEvents = domain.pollAllEvents();
            if (domainEvents != null && !domainEvents.isEmpty()) {
                domainEvents.forEach(entity::recordPersistenceEvent);
            }

            imageFileMetaJpaCommandRepository.save(entity);
        }
    }
}

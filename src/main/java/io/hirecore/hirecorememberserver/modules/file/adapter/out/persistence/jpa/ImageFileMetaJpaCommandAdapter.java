package io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.entity.ImageFileMetaJpaEntity;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.mapper.ImageFileMetaJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.repository.ImageFileMetaJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.repository.ImageFileMetaJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.SaveImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.UpdateImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    public List<ImageFileMeta> updateAllUploadStatus(List<ImageFileMeta> imageFileMetas) {
        if (imageFileMetas == null || imageFileMetas.isEmpty()) return List.of();

        List<ImageFileMetaJpaEntity> updatedImageFileMetaEntities = new ArrayList<>();
        for (ImageFileMeta domain : imageFileMetas) {
            imageFileMetaJpaQueryRepository.findById(domain.getId())
                    .ifPresent(matchedEntity -> {
                        matchedEntity.updateUploadStatus(domain.getUploadStatus(), domain.getCompletedUploadAt());
                        updatedImageFileMetaEntities.add(matchedEntity);
                    });
        }

        return updatedImageFileMetaEntities.stream()
                .map(imageFileMetaJpaEntityMapper::toDomain)
                .collect(Collectors.toList());
    }
}

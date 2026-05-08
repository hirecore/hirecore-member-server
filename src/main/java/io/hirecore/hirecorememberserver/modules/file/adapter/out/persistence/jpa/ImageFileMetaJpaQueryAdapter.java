package io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.mapper.ImageFileMetaJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.repository.ImageFileMetaJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.LoadImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageFileMetaJpaQueryAdapter implements LoadImageFileMetaPort {

    private final ImageFileMetaJpaQueryRepository imageFileMetaJpaQueryRepository;
    private final ImageFileMetaJpaEntityMapper imageFileMetaJpaEntityMapper;

    @Override
    public List<ImageFileMeta> findAllByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }

        return imageFileMetaJpaQueryRepository.findAllByIdIn(ids)
                .stream()
                .map(imageFileMetaJpaEntityMapper::toDomain)
                .toList();
    }
}

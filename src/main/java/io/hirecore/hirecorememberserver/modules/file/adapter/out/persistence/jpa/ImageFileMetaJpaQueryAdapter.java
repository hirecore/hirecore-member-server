package io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.mapper.ImageFileMetaJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.repository.ImageFileMetaJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.LoadImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.UploadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<ImageFileMeta> findById(Long id) {
        return imageFileMetaJpaQueryRepository.findById(id)
                .map(imageFileMetaJpaEntityMapper::toDomain);
    }

    @Override
    public List<ImageFileMeta> findOrphanedCandidates(Instant orphanedBefore, int limit) {
        return imageFileMetaJpaQueryRepository
                .findAllByUploadStatusAndOrphanedAtBeforeOrderByOrphanedAtAsc(
                        UploadStatus.ORPHANED,
                        orphanedBefore,
                        PageRequest.of(0, limit)
                )
                .stream()
                .map(imageFileMetaJpaEntityMapper::toDomain)
                .toList();
    }
}

package io.hirecore.hirecorememberserver.modules.file.application.port.out;

import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface LoadImageFileMetaPort {
    List<ImageFileMeta> findAllByIds(List<Long> ids);
    Optional<ImageFileMeta> findById(Long id);

    // 청소 워커용: orphanedBefore 이전 ORPHANED 메타를 orphanedAt 오름차순 최대 limit 건
    List<ImageFileMeta> findOrphanedCandidates(Instant orphanedBefore, int limit);
}

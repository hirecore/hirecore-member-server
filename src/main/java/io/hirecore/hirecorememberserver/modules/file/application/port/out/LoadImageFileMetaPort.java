package io.hirecore.hirecorememberserver.modules.file.application.port.out;

import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface LoadImageFileMetaPort {
    List<ImageFileMeta> findAllByIds(List<Long> ids);
    Optional<ImageFileMeta> findById(Long id);

    /**
     * ORPHANED 상태이면서 {@code orphanedAt} 이 {@code orphanedBefore} 이전인 메타를
     * {@code orphanedAt} 오름차순으로 최대 {@code limit} 건 조회합니다.
     * 스토리지 청소 워커의 후보 조회용입니다.
     */
    List<ImageFileMeta> findOrphanedCandidates(Instant orphanedBefore, int limit);
}

package io.hirecore.hirecorememberserver.modules.file.application.port.out;

import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;

import java.util.List;

public interface UpdateImageFileMetaPort {
    // UPLOADED 전이 + save() 로 @DomainEvents 발행
    void markAllAsUploaded(List<ImageFileMeta> imageFileMetas);

    // ORPHANED 전이 + save() 로 @DomainEvents 발행
    void markAllAsOrphaned(List<ImageFileMeta> imageFileMetas);

    // DELETED 전이 (청소 워커 전용, 종결 상태라 이벤트 없음)
    void markAllAsDeleted(List<ImageFileMeta> imageFileMetas);
}

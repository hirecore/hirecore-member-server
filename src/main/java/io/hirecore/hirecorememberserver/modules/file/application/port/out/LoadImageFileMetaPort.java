package io.hirecore.hirecorememberserver.modules.file.application.port.out;

import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;

import java.util.List;
import java.util.Optional;

public interface LoadImageFileMetaPort {
    List<ImageFileMeta> findAllByIds(List<Long> ids);
    Optional<ImageFileMeta> findById(Long id);
}

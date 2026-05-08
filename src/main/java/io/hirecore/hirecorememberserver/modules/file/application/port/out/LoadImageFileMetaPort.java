package io.hirecore.hirecorememberserver.modules.file.application.port.out;

import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;

import java.util.List;

public interface LoadImageFileMetaPort {
    List<ImageFileMeta> findAllByIds(List<Long> ids);
}

package io.hirecore.hirecorememberserver.modules.file.application.port.out;

import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;

import java.util.List;

public interface UpdateImageFileMetaPort {
    List<ImageFileMeta> updateAllUploadStatus(List<ImageFileMeta> imageFileMetas);
}

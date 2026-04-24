package io.hirecore.hirecorememberserver.modules.file.application.port.out;

import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;

public interface SaveImageFileMetaPort {
    ImageFileMeta save(ImageFileMeta imageFileMeta);
}

package io.hirecore.hirecorememberserver.modules.file.application.port.in;

import java.util.Collection;

public interface UpdateUploadStatusOfImageFileMetaUseCase {
    void execute(Long memberAccountId, Collection<Long> imageIds);
}

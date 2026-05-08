package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

import java.util.Collection;

public interface UpdateUploadStatusOfImageFileMetaPort {
    void markUploaded(Long memberAccountId, Collection<Long> imageIds);
}

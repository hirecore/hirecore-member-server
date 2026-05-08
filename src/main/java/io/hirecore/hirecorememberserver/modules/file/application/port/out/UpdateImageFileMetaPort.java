package io.hirecore.hirecorememberserver.modules.file.application.port.out;

import java.util.Collection;

public interface UpdateImageFileMetaPort {
    void markAllAsUploaded(Collection<Long> imageIds);
}

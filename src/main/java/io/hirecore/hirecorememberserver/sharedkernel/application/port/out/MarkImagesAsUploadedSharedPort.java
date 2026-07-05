package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

import java.util.Collection;

public interface MarkImagesAsUploadedSharedPort {
    void markUploaded(Long memberAccountId, Collection<Long> imageIds);
}

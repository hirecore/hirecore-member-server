package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

import java.util.Collection;

public interface MarkImagesAsUploadedPort {
    void markUploaded(Long memberAccountId, Collection<Long> imageIds);
}

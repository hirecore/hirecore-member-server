package io.hirecore.hirecorememberserver.modules.file.application.port.in;

import java.util.Collection;

public interface MarkImageFileMetasAsUploadedUseCase {
    void execute(Long memberAccountId, Collection<Long> imageIds);
}

package io.hirecore.hirecorememberserver.modules.file.application.port.in;

import java.util.Collection;

public interface MarkImageFileMetasAsOrphanedUseCase {
    // UPLOADED → ORPHANED 전이 (소유권 검증, 이미 ORPHANED 는 멱등 통과)
    void execute(Long memberAccountId, Collection<Long> imageIds);
}

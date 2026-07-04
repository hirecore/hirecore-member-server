package io.hirecore.hirecorememberserver.modules.file.application.port.in;

public interface CleanupOrphanedImageUseCase {
    // grace period 경과 ORPHANED 후보를 batch size 만큼 실물 삭제 후 DELETED 전이, 마킹 개수 반환
    int execute();
}

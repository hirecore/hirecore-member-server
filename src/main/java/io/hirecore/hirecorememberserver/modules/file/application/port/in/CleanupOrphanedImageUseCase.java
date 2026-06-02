package io.hirecore.hirecorememberserver.modules.file.application.port.in;

public interface CleanupOrphanedImageUseCase {
    /**
     * grace period 가 경과한 ORPHANED 이미지 후보를 한 주기 분량(batch size) 만큼 조회하여
     * 스토리지에서 실물 객체를 삭제하고, 성공한 메타를 DELETED 로 전이합니다.
     *
     * @return 본 주기에 DELETED 로 마킹된 메타 개수
     */
    int execute();
}

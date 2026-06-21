package io.hirecore.hirecorememberserver.modules.file.application.port.in;

import java.util.Collection;

public interface MarkImageFileMetasAsOrphanedUseCase {
    /**
     * 전달받은 이미지 식별자들의 {@code ImageFileMeta} 를 UPLOADED → ORPHANED 로 전이합니다.
     *
     * <p>각 메타가 호출자 소유인지 검증하며, 누락된 메타가 있으면 IMAGE_NOT_FOUND 예외가 발생합니다.
     * 이미 ORPHANED 인 메타는 도메인 멱등 처리로 추가 이벤트 없이 통과합니다.</p>
     */
    void execute(Long memberAccountId, Collection<Long> imageIds);
}

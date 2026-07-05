package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

import java.util.Optional;

public interface LoadImageUrlSharedPort {
    /**
     * 이미지 식별자로 공개 URL 을 조회합니다.
     *
     * <p>{@code UploadStatus == UPLOADED} 인 이미지에 대해서만 URL 을 반환합니다.
     * PENDING / ORPHANED / DELETED 상태이거나 메타가 없으면 빈 {@link Optional} 을 반환합니다.</p>
     */
    Optional<String> findUrlById(Long imageId);
}
